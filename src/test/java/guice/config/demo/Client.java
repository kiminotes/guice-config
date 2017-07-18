package guice.config.demo;

import javax.inject.Inject;
import java.util.Set;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-07-18
 */
public class Client {

    final Set<Hello> hello;
    final HelloWorld helloWorld;

    @Inject
    public Client(Set<Hello> hello, HelloWorld helloWorld) {
        this.hello = hello;
        this.helloWorld = helloWorld;
    }

}
