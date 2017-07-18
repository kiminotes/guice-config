package guice.config.demo;

import guice.config.XmlConfiguration;
import com.google.inject.Injector;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-07-18
 */
public class ClientMain {

    public static void main(String[] args) throws Exception {
        XmlConfiguration configuration = new XmlConfiguration("demo.xml");
        Injector injector = configuration.build();
        Client client = injector.getInstance(Client.class);
        System.out.println(client.hello);
        System.out.println(client.helloWorld);
    }

}