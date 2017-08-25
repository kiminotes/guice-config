package guice.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-26
 */
class XmlConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(XmlConfiguration.class);

    String[] resources;

    final InjectorBuilder builder = new InjectorBuilder();

    public XmlConfiguration(String resource) throws Exception {
        this(resource, null);
    }

    public XmlConfiguration(String resource, ClassLoader classLoader) throws Exception {
        init(new String[]{resource}, classLoader);
    }

    public XmlConfiguration(String[] resource) throws Exception {
        init(resource, null);
    }

    public XmlConfiguration(String[] resources, ClassLoader classLoader) throws Exception {
        init(resources, classLoader);
    }

    public Injector build() {
        return builder.build();
    }

    public Module module() {
        return builder.module();
    }

    void init(String[] resources, ClassLoader classLoader) throws Exception {
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        this.resources = resources;
        for (String resource : resources) {
            Enumeration<URL> enumeration = classLoader.getResources(resource);
            while (enumeration.hasMoreElements()) {
                loadConfig(enumeration.nextElement(), classLoader);
            }
        }
    }

    void loadConfig(URL config, ClassLoader loader) throws Exception {
        InputStream is;
        Document doc;
        is = config.openStream();
        try {
            doc = loadDocument(is);
        } finally {
            is.close();
        }

        parse(new ParseContext(config, doc, loader));
    }

    void parse(ParseContext context) throws ClassNotFoundException {
        Element root = context.document.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                context.element = (Element) node;
                if ("selector".equals(context.element.getTagName())) {
                    parseProperty(context);
                } else if ("binding".equals(context.element.getTagName())) {
                    parseBinding(context);
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Unsupported element " + context.element.getTagName() + " in " + context.url);
                    }
                }
            }
        }
    }

    void parseProperty(ParseContext context) {
        String key = context.element.getAttribute("type");
        String value = context.element.getAttribute("selection");
        final Object old = builder.addSelection(key, value);
        if (old != null
            && errorIfDup()) {
            throw new IllegalStateException("Duplicate property " + key + " in " + context.url);
        }
    }

    boolean errorIfDup() {
        return Boolean.TRUE.toString().equalsIgnoreCase(
            System.getProperty("error.if.duplicate.property", Boolean.TRUE.toString()));
    }

    void parseBinding(ParseContext context) throws ClassNotFoundException {
        final String type = getAttribute(context.element, "type");
        final String id = getAttribute(context.element, "id");
        final String implementation = getAttribute(context.element, "implementation");
        final String scope = getAttribute(context.element, "scope");
        final String name = getAttribute(context.element, "name");

        if (StringUtils.isBlank(type)) {
            throw new IllegalStateException("type is empty in " + context.url);
        }

        Class<?> typeClass = loadClass(context, type);
        Class<?> implementationClass;
        if (StringUtils.isNotBlank(implementation)) {
            implementationClass = loadClass(context, implementation);
            if (!typeClass.isAssignableFrom(implementationClass)) {
                throw new IllegalStateException("class " + implementation + " is not subclass of "
                    + type + " in " + context.url);
            }
        } else {
            implementationClass = typeClass;
        }

        BindingConfig binding = new BindingConfig();
        binding.setType(typeClass);
        binding.setId(id);
        binding.setImplementation(implementationClass);
        binding.setScope(scope);
        binding.setName(name);
        builder.register(binding);
    }

    Class<?> loadClass(ParseContext context, String name) throws ClassNotFoundException {
        if (context.classLoader != null) {
            return ClassUtils.getClass(context.classLoader, name);
        } else {
            return ClassUtils.getClass(name);
        }
    }

    String getAttribute(Element element, String attributeName) {
        final String attributeValue = element.getAttribute(attributeName);
        if (StringUtils.isBlank(attributeValue)) {
            return null;
        } else {
            return attributeValue;
        }
    }

    Document loadDocument(InputStream is) throws Exception {
        // FIXME add schema validate and line number support
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    class ParseContext {
        final URL url;
        final Document document;
        final ClassLoader classLoader;
        Element element;

        public ParseContext(URL url, Document document, ClassLoader classLoader) {
            this.url = url;
            this.document = document;
            this.classLoader = classLoader;
        }
    }

}
