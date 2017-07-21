package guice.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-26
 */
public class InjectorBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InjectorBuilder.class);

    final List<BindingConfig> bindings   = new ArrayList<>();
    final Properties          properties = new Properties();

    Injector injector;

    public InjectorBuilder() {
        super();
    }

    public void register(BindingConfig binding) {
        if (bindings.contains(binding)) {
            throw new IllegalStateException("Duplicate bindings " + binding);
        }
        bindings.add(binding);
    }

    public Object setProperty(String key, String value) {
        return properties.setProperty(key, value);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public synchronized Injector build() {
        if (injector != null) {
            return injector;
        }

        final BindingSelector selector = new BindingSelector(properties);
        final List<BindingConfig> selectedBindings = selector.select(bindings);
        final Map<Class<?>, List<BindingConfig>> map = convert(selectedBindings);
        showBindings(map);
        final Module module = new AbstractModule() {
            @Override
            protected void configure() {
                for (Map.Entry<Class<?>, List<BindingConfig>> entry : map.entrySet()) {
                    final Class<?> key = entry.getKey();
                    final List<BindingConfig> value = entry.getValue();
                    // FIXME scope
                    if (value.size() == 1) {
                        final BindingConfig binding = value.get(0);
                        if (binding.getType().equals(binding.getImplementation())) {
                            bind(binding.getType());
                        } else {
                            bind(binding.getType()).to((Class) binding.getImplementation());
                        }
                    } else if (value.size() > 1) {
                        Multibinder multibinder = Multibinder.newSetBinder(binder(), (Class) key);
                        for (int i = 0; i < value.size(); i++) {
                            multibinder.addBinding().to(value.get(i).getImplementation());
                        }
                    } else {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("Unknown how to config bindings: " + key + " -> " + value);
                        }
                    }
                }
            }
        };
        injector = Guice.createInjector(module);
        return injector;
    }

    Map<Class<?>, List<BindingConfig>> convert(List<BindingConfig> bindings) {
        final Map<Class<?>, List<BindingConfig>> result = new HashMap<>();
        for (int i = 0; i < bindings.size(); i++) {
            final BindingConfig binding = bindings.get(i);
            List<BindingConfig> list = result.get(binding.getType());
            if (list == null) {
                result.put(binding.getType(), new ArrayList<>());
                list = result.get(binding.getType());
            }
            list.add(binding);
        }
        return result;
    }

    void showBindings(final Map<Class<?>, List<BindingConfig>> map) {
        if (Boolean.getBoolean("show.bindings")) {
            System.out.println(buildBindingString(map));
        }
    }

    String buildBindingString(final Map<Class<?>, List<BindingConfig>> map) {
        final StringBuilder buff = new StringBuilder(128);
        final int max = maxLength(map.keySet());
        for (final Map.Entry<Class<?>, List<BindingConfig>> entry : map.entrySet()) {
            buff.append(entry.getKey().getName());
            Util.appendIndent(buff, max - entry.getKey().getName().length());
            buff.append(" -> ")
                .append(Util.implementationsString(entry.getValue()))
                .append(Util.NL);
        }
        return buff.toString();
    }

    int maxLength(final Collection<Class<?>> collection) {
        int max = 0;
        for (Iterator<Class<?>> iterator = collection.iterator(); iterator.hasNext();) {
            max = Math.max(max, iterator.next().getName().length());
        }
        return max;
    }

}
