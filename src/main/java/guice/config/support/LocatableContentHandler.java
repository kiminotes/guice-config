package guice.config.support;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-25
 */
public class LocatableContentHandler implements ContentHandler {

    public static final String LOCATION_URI              = "uri";
    public static final String LOCATION_ATTRIBUTE_LINE   = "line";
    public static final String LOCATION_ATTRIBUTE_COLUMN = "column";
    public static final String LOCATION_LINE_Q_NAME      = "loc:line";
    public static final String LOCATION_COLUMN_Q_NAME    = "loc:column";

    ContentHandler nextHandler;
    Locator        locator;

    public LocatableContentHandler(ContentHandler nextHandler) {
        if (nextHandler == null) {
            throw new NullPointerException("nextHandler is null");
        }
        this.nextHandler = nextHandler;
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
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
        nextHandler.startElement(uri, localName, qName, addLocation(locator, atts));
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

    Attributes addLocation(Locator locator, Attributes attrs) {
        if (locator == null
            || attrs.getIndex(LOCATION_URI, LOCATION_ATTRIBUTE_LINE) != -1) {
            return attrs;
        }

        // Get an AttributeImpl so that we can add new attributes.
        AttributesImpl newAttrs = attrs instanceof AttributesImpl ?
            (AttributesImpl) attrs : new AttributesImpl(attrs);

        newAttrs.addAttribute(
            LOCATION_URI,
            LOCATION_ATTRIBUTE_LINE,
            LOCATION_LINE_Q_NAME,
            "CDATA",
            Integer.toString(locator.getLineNumber()));
        newAttrs.addAttribute(
            LOCATION_URI,
            LOCATION_ATTRIBUTE_COLUMN,
            LOCATION_COLUMN_Q_NAME,
            "CDATA",
            Integer.toString(locator.getColumnNumber()));

        return newAttrs;
    }

}
