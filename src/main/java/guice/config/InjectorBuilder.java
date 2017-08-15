package guice.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-26
 */
public class InjectorBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InjectorBuilder.class);

    public static Injector buildInjector(String... resources) throws Exception {
        return buildInjector(resources, null);
    }

    public static Injector buildInjector(String[] resources, ClassLoader classLoader) throws Exception {
        return new XmlConfiguration(resources, classLoader).build();
    }

    public static Module buildModule(String... resources) throws Exception {
        return buildModule(resources, null);
    }

    public static Module buildModule(String[] resources, ClassLoader classLoader) throws Exception {
        return new XmlConfiguration(resources, classLoader).module();
    }

    final List<BindingConfig> bindings   = new ArrayList<>();
    final Properties          properties = new Properties();

    Module   module;
    Injector injector;

    InjectorBuilder() {
        super();
    }

    synchronized void register(BindingConfig binding) {
        if (bindings.contains(binding)) {
            throw new IllegalStateException("Duplicate bindings " + binding);
        }
        bindings.add(binding);
    }

    synchronized Object setProperty(String key, String value) {
        return properties.setProperty(key, value);
    }

    String getProperty(String key) {
        return properties.getProperty(key);
    }

    synchronized Module module() {
        if (module != null) {
            return module;
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
                        buildBinding(binder(), value.get(0));
                    } else if (value.size() > 1) {
                        buildMultibinding(binder(), entry.getKey(), entry.getValue());
                    } else {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("Unknown how to config bindings: " + key + " -> " + value);
                        }
                    }
                }
            }
        };
        return module;
    }

    synchronized Injector build() {
        if (injector != null) {
            return injector;
        }

        final Module module = module();
        injector = Guice.createInjector(module);
        return injector;
    }

    void buildBinding(Binder binder, BindingConfig binding) {
        AnnotatedBindingBuilder annotatedBindingBuilder = binder.bind(binding.getType());
        LinkedBindingBuilder linkedBindingBuilder = annotatedBindingBuilder;
        if (StringUtils.isNotBlank(binding.getName())) {
            linkedBindingBuilder = annotatedBindingBuilder.annotatedWith(Names.named(binding.getName()));
        }

        if (!binding.getType().equals(binding.getImplementation())) {
            linkedBindingBuilder.to(binding.getImplementation());
        }
    }

    void buildMultibinding(Binder binder, Class<?> type, List<BindingConfig> list) {
        Multibinder multibinder = Multibinder.newSetBinder(binder, (Class) type);
        for (int i = 0; i < list.size(); i++) {
            multibinder.addBinding().to(list.get(i).getImplementation());
        }
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
