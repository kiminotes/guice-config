package guice.config.support;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-26
 */
public class DefaultHandlerDelegate extends DefaultHandler {
    ContentHandler nextHandler;

    public DefaultHandlerDelegate(ContentHandler next) {
        nextHandler = next;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        nextHandler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        nextHandler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        nextHandler.endDocument();
    }

    @Override
    public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
        nextHandler.startElement(uri, loc, raw, attrs);
    }

    @Override
    public void endElement(String arg0, String arg1, String arg2) throws SAXException {
        nextHandler.endElement(arg0, arg1, arg2);
    }

    @Override
    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
        nextHandler.startPrefixMapping(arg0, arg1);
    }

    @Override
    public void endPrefixMapping(String arg0) throws SAXException {
        nextHandler.endPrefixMapping(arg0);
    }

    @Override
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        nextHandler.characters(arg0, arg1, arg2);
    }

    @Override
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        nextHandler.ignorableWhitespace(arg0, arg1, arg2);
    }

    @Override
    public void processingInstruction(String arg0, String arg1) throws SAXException {
        nextHandler.processingInstruction(arg0, arg1);
    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
        nextHandler.skippedEntity(arg0);
    }
}
