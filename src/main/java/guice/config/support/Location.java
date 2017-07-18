package guice.config.support;

import java.io.Serializable;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-25
 */
public class Location implements Serializable {
    private static final long serialVersionUID = 4520671789326153572L;

    final int lineNumber;
    final int columnNumber;

    Location(int lineNumber, int columnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
}
