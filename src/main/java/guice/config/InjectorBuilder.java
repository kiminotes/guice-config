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

    public static Injector buildInjector(List<BindingConfig> bindings) {
        final InjectorBuilder builder = create(bindings);
        return builder == null ? null : builder.build();
    }

    public static Module buildModule(List<BindingConfig> bindings) {
        final InjectorBuilder builder = create(bindings);
        return builder == null ? null : builder.module;
    }

    static InjectorBuilder create(List<BindingConfig> bindings) {
        if (bindings == null
            || bindings.isEmpty()) {
            return null;
        }

        final InjectorBuilder builder = new InjectorBuilder();
        for (int i = 0; i < bindings.size(); i++) {
            builder.register(bindings.get(i));
        }
        return builder;
    }

    final List<BindingConfig> bindings   = new ArrayList<>();
    final Properties          selections = new Properties();

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

    synchronized Object addSelection(String type, String selection) {
        return selections.setProperty(type, selection);
    }

    String getSelection(String type) {
        return selections.getProperty(type);
    }

    synchronized Module module() {
        if (module != null) {
            return module;
        }

        final BindingSelector selector = new BindingSelector(selections);
        final List<BindingConfig> selectedBindings = selector.select(bindings);
        final Map<BindingKey, List<BindingConfig>> map = convert(selectedBindings);
        showBindings(map);
        final Module module = new AbstractModule() {
            @Override
            protected void configure() {
                for (Map.Entry<BindingKey, List<BindingConfig>> entry : map.entrySet()) {
                    final BindingKey key = entry.getKey();
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

    void buildMultibinding(Binder binder, BindingKey key, List<BindingConfig> list) {
        Multibinder multibinder = Multibinder.newSetBinder(binder, (Class) key.type);
        for (int i = 0; i < list.size(); i++) {
            multibinder.addBinding().to(list.get(i).getImplementation());
        }
    }

    Map<BindingKey, List<BindingConfig>> convert(List<BindingConfig> bindings) {
        final Map<BindingKey, List<BindingConfig>> result = new HashMap<>(bindings.size());
        for (int i = 0; i < bindings.size(); i++) {
            final BindingConfig binding = bindings.get(i);
            final BindingKey key = new BindingKey(binding.getType(), binding.getName());
            List<BindingConfig> list = result.get(key);
            if (list == null) {
                result.put(key, new ArrayList<>());
                list = result.get(key);
            }
            list.add(binding);
        }
        return result;
    }

    void showBindings(final Map<BindingKey, List<BindingConfig>> map) {
        if (Boolean.getBoolean("show.bindings")) {
            System.out.println(buildBindingString(map));
        }
    }

    String buildBindingString(final Map<BindingKey, List<BindingConfig>> map) {
        final StringBuilder buff = new StringBuilder(128);
        final int max = maxLength(map.keySet());
        for (final Map.Entry<BindingKey, List<BindingConfig>> entry : map.entrySet()) {
            buff.append(entry.getKey().type.getName());
            Util.appendIndent(buff, max - entry.getKey().type.getName().length());
            buff.append(" -> ")
                .append(Util.implementationsString(entry.getValue()))
                .append(Util.NL);
        }
        return buff.toString();
    }

    int maxLength(final Collection<BindingKey> collection) {
        int max = 0;
        for (Iterator<BindingKey> iterator = collection.iterator(); iterator.hasNext();) {
            max = Math.max(max, iterator.next().type.getName().length());
        }
        return max;
    }

    class BindingKey {
        final Class<?> type;
        final String name;

        public BindingKey(Class<?> type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BindingKey that = (BindingKey) o;

            if (!type.equals(that.type)) return false;
            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

}
