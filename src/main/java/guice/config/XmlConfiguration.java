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
                loadConfig(builder, enumeration.nextElement());
            }
        }
    }

    void loadConfig(InjectorBuilder builder, URL config) throws Exception {
        InputStream is;
        Document doc;
        is = config.openStream();
        try {
            doc = loadDocument(is);
        } finally {
            is.close();
        }

        parse(builder, doc);
    }

    void parse(InjectorBuilder builder, Document doc) throws ClassNotFoundException {
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                if ("property".equals(element.getTagName())) {
                    parseProperty(builder, element);
                } else if ("binding".equals(element.getTagName())) {
                    parseBinding(builder, element);
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Unsupported element " + element.getTagName());
                    }
                }
            }
        }
    }

    void parseProperty(InjectorBuilder builder, Element element) {
        String key = element.getAttribute("key");
        String value = element.getAttribute("value");
        final Object old = builder.setProperty(key, value);
        if (old != null
            && errorIfDup()) {
            throw new IllegalStateException("Duplicate property " + key);
        }
    }

    boolean errorIfDup() {
        return Boolean.TRUE.toString().equalsIgnoreCase(
            System.getProperty("error.if.duplicate.property", Boolean.TRUE.toString()));
    }

    void parseBinding(InjectorBuilder builder, Element element) throws ClassNotFoundException {
        final String type = getAttribute(element, "type");
        final String id = getAttribute(element, "id");
        final String implementation = getAttribute(element, "implementation");
        final String scope = getAttribute(element, "scope");
        final String name = getAttribute(element, "name");

        if (StringUtils.isBlank(type)) {
            throw new IllegalStateException("type is empty");
        }

        Class<?> typeClass = ClassUtils.getClass(type);
        Class<?> implementationClass;
        if (StringUtils.isNotBlank(implementation)) {
            implementationClass = ClassUtils.getClass(implementation);
            if (!typeClass.isAssignableFrom(implementationClass)) {
                throw new IllegalStateException("class " + implementation + " is not subclass of " + type);
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

    String getAttribute(Element element, String attributeName) {
        final String attributeValue = element.getAttribute(attributeName);
        if (StringUtils.isBlank(attributeValue)) {
            return null;
        } else {
            return attributeValue;
        }
    }

    Document loadDocument(InputStream is) throws Exception {
        // FIXME add schema validate
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

}
