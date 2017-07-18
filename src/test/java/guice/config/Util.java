package guice.config;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-07-18
 */
final class Util {
    private Util() {
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
}
