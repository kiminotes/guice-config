package guice.config;

import java.util.List;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a> 2017-08-25
 */
public class ExtensionFactoryBuilder {

    public static ExtensionFactory getExtensionFactory(String... resources) throws Exception {
        return new DefaultExtensionFactory(InjectorBuilder.buildInjector(resources));
    }

    public static ExtensionFactory getExtensionFactory(String[] resources, ClassLoader classLoader) throws Exception {
        return new DefaultExtensionFactory(InjectorBuilder.buildInjector(resources, classLoader));
    }

    public static ExtensionFactory getExtensionFactory(final List<BindingConfig> bindings) {
        return new DefaultExtensionFactory(InjectorBuilder.buildInjector(bindings));
    }

    ExtensionFactoryBuilder() {
    }

}
