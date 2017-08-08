package guice.config;

import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import guice.config.demo.Hello;
import guice.config.demo.HelloA;
import guice.config.demo.HelloB;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a> 2017-08-03
 */
public class InjectorNamedTest {

    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Hello.class).annotatedWith(Names.named("a")).to(HelloA.class);
                bind(Hello.class).annotatedWith(Names.named("b")).to(HelloB.class);
            }
        });

        Hello hello = injector.getInstance(Key.get(Hello.class, Names.named("a")));
        System.out.println(hello);
        hello = injector.getInstance(Key.get(Hello.class, Names.named("b")));
        System.out.println(hello);

        final Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        for (Map.Entry<Key<?>, Binding<?>> entry : bindings.entrySet()) {
            System.out.println(entry.getKey() + " --> " + entry.getValue());
        }
    }

}
