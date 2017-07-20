package guice.config;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXSource;
import java.io.InputStream;

import guice.config.support.LocatableContentHandler;
import guice.config.support.XmlUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-25
 */
@Ignore
public class LineNumberTest {

    @Test
    public void test_parse() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("guice-config-demo.xml");
        Document document = XmlUtil.parse(is);
        Element root = document.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                printElement(element);
            }
        }
    }

    void printElement(Element element) {
        if (element == null) {
            return;
        }
        System.out.printf("%s at %s, %s%n",
            element.getTagName(),
            element.getAttribute(LocatableContentHandler.LOCATION_LINE_Q_NAME),
            element.getAttribute(LocatableContentHandler.LOCATION_COLUMN_Q_NAME));
    }

    @Test
    public void test_staxToDom() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("guice-config-demo.xml");
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(is);

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMResult result = new DOMResult();
        transformer.transform(new StAXSource(xmlEventReader), result);
        Node node = result.getNode();
        System.out.println(node);
    }

    @Test
    public void test_sat() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("guice-config-demo.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        System.out.println(document);
    }

    @Test
    public void test_stax() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("guice-config-demo.xml");
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader eventReader = factory.createXMLEventReader(is);
        while (eventReader.hasNext()) {
            XMLEvent xmlEvent = eventReader.nextEvent();
            switch (xmlEvent.getEventType()) {
                case XMLStreamConstants.ATTRIBUTE:
                    System.out.println("attribute");
                    break;
                case XMLStreamConstants.CDATA:
                    System.out.println("cdata");
                    break;
                case XMLStreamConstants.CHARACTERS:
                    System.out.println("characters");
                    break;
                case XMLStreamConstants.COMMENT:
                    System.out.println("comment");
                    break;
                case XMLStreamConstants.DTD:
                    System.out.println("dtd");
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    System.out.println("end_document");
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    System.out.println("end_element");
                    break;
                case XMLStreamConstants.ENTITY_DECLARATION:
                    System.out.println("entity_declaration");
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    System.out.println("entity_reference");
                    break;
                case XMLStreamConstants.NAMESPACE:
                    System.out.println("namespace");
                    break;
                case XMLStreamConstants.NOTATION_DECLARATION:
                    System.out.println("notation_declaration");
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    System.out.println("processing_instruction");
                    break;
                case XMLStreamConstants.SPACE:
                    System.out.println("space");
                    break;
                case XMLStreamConstants.START_DOCUMENT:
                    System.out.println("start_document");
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    System.out.println("start_element");
                    break;
                default:
                    System.out.println("ERROR");
            }
        }
    }

}
