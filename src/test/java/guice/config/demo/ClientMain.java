package guice.config.demo;

import guice.config.InjectorBuilder;
import com.google.inject.Injector;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-07-18
 */
public class ClientMain {

    public static void main(String[] args) throws Exception {
        System.setProperty("show.bindings", Boolean.TRUE.toString());
        Injector injector = InjectorBuilder.buildInjector("demo.xml");
        Client client = injector.getInstance(Client.class);
        System.out.println(client.hello);
        System.out.println(client.helloWorld);
    }

}
