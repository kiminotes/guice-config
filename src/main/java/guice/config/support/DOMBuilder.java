package guice.config.support;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-25
 */
public class DOMBuilder implements ContentHandler {

    ContentHandler nextHandler;
    DOMResult      result;

    public DOMBuilder() {
        init();
    }

    void init() {
        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler transformerHandler;
        try {
            transformerHandler = factory.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        result = new DOMResult();
        transformerHandler.setResult(result);
        nextHandler = transformerHandler;
    }

    public void setDocumentLocator(Locator locator) {
        nextHandler.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
        nextHandler.startDocument();
    }

    public void endDocument() throws SAXException {
        nextHandler.endDocument();
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        nextHandler.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        nextHandler.endPrefixMapping(prefix);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        nextHandler.startElement(uri, localName, qName, atts);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        nextHandler.endElement(uri, localName, qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        nextHandler.characters(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        nextHandler.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        nextHandler.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
        nextHandler.skippedEntity(name);
    }

    public Document getDocument() {
        if (this.result == null || this.result.getNode() == null) {
            return null;
        } else if (this.result.getNode().getNodeType() == Node.DOCUMENT_NODE) {
            return (Document) this.result.getNode();
        } else {
            return this.result.getNode().getOwnerDocument();
        }
    }
}
