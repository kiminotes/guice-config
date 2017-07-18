package guice.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-07-18
 */
public class SchemaTest {

    @Test
    public void test_schema() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                InputSource r = new InputSource(getClass().getClassLoader().getResourceAsStream("guice-config.xsd"));
                r.setSystemId(systemId);
                r.setPublicId(publicId);
                return r;
            }
        });
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                System.out.println("warning");
                exception.printStackTrace();
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                System.out.println("error");
                exception.printStackTrace();
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                System.out.println("fatalError");
                exception.printStackTrace();
            }
        });
        Document document = builder.parse(getClass().getClassLoader().getResourceAsStream("guice-config-demo.xml"));
        System.out.println(document);
    }

}
