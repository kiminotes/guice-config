package guice.config;

import javax.inject.Inject;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import guice.config.demo.Hello;
import guice.config.demo.HelloA;
import guice.config.demo.HelloB;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a> 2017-08-16
 */
public class InjectorBuilderTest {

    @Test(expected = ConfigurationException.class)
    public void test_InjectWithoutName() throws Exception {
        final List<BindingConfig> bindings = createBindings();

        final Injector injector = InjectorBuilder.buildInjector(bindings);
        injector.getInstance(Client.class);
    }

    @Test
    public void test_InjectWithName() throws Exception {
        final List<BindingConfig> bindings = createBindings();

        final Injector injector = InjectorBuilder.buildInjector(bindings);

        assertNotNull(injector.getBinding(Key.get(Hello.class, Names.named("a"))));
        assertNotNull(injector.getBinding(Key.get(Hello.class, Names.named("b"))));
    }

    List<BindingConfig> createBindings() {
        final List<BindingConfig> bindings = new ArrayList<>();
        bindings.add(Util.create(Hello.class, "a", HelloA.class, null, "a"));
        bindings.add(Util.create(Hello.class, "b", HelloB.class, null, "b"));
        return bindings;
    }

    static class Client {
        @Inject
        Hello hello;
    }

    void printBinding(Map<Key<?>, Binding<?>> map, PrintStream out) {
        for (Map.Entry<Key<?>, Binding<?>> entry : map.entrySet()) {
            out.printf("{%s, %s} -> %s %n", entry.getKey().getTypeLiteral(), entry.getKey().getAnnotation(),
                entry.getValue());
        }
    }

}