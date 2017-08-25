package guice.config;

import javax.inject.Inject;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a> 2017-08-21
 */
class DefaultExtensionFactory implements ExtensionFactory {

    Injector injector;

    @Inject
    DefaultExtensionFactory(final Injector injector) {
        this.injector = injector;
    }

    public <T> T getExtension(Class<T> type, String name) {
        try {
            return injector.getInstance(Key.get(type, Names.named(name)));
        } catch (ConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> boolean containsExtension(Class<T> type, String name) {
        return injector.getExistingBinding(Key.get(type, Names.named(name))) != null;
    }
}
