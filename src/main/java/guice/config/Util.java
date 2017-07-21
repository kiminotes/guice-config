package guice.config;

import java.util.List;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-07-20
 */
class Util {

    public static final String NL = System.getProperty("line.separator");

    public static String implementationsString(final List<BindingConfig> list) {
        if (list == null
            || list.isEmpty()) {
            return "";
        }

        final char separator = ',';
        final StringBuilder buf = new StringBuilder(64);
        buf.append('{');
        for (int i = 0; i < list.size(); i++) {
            final BindingConfig binding = list.get(i);
            buf.append(binding.getId())
                .append(":")
                .append(binding.getImplementation().getName())
                .append(separator);
        }

        if (buf.length() > 1) {
            buf.setLength(buf.length() - 1);
        }
        buf.append('}');
        return buf.toString();
    }

    public static StringBuilder appendIndent(final StringBuilder buf, int number) {
        if (number > 0) {
            for (int i = 0; i < number; i++) {
                buf.append(' ');
            }
        }
        return buf;
    }

    public static BindingConfig create(Class<?> type, String id, Class<?> impl) {
        return create(type, id, impl, null);
    }

    public static BindingConfig create(Class<?> type, String id, Class<?> impl, String scope) {
        BindingConfig r = new BindingConfig();
        r.setType(type);
        r.setId(id);
        r.setImplementation(impl);
        r.setScope(scope);
        return r;
    }

    private Util() {
    }
}
