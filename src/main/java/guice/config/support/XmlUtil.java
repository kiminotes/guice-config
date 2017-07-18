package guice.config.support;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-25
 */
public final class XmlUtil {

    private XmlUtil() {
    }

    public static Document parse(InputStream is) throws IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            throw new IOException(e);
        }

        DOMBuilder builder = new DOMBuilder();
        try {
            parser.parse(is, new DefaultHandlerDelegate(new LocatableContentHandler(builder)));
        } catch (SAXException e) {
            throw new IOException(e);
        }

        return builder.getDocument();
    }

}
