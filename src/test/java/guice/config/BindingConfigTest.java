package guice.config;

import java.util.ArrayList;
import java.util.List;

import guice.config.demo.Hello;
import guice.config.demo.HelloA;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-07-18
 */
public class BindingConfigTest {

    @Test
    public void test_equals() throws Exception {
        // type, implementation
        BindingConfig bindingA = Util.create(Hello.class, "a", HelloA.class);
        BindingConfig bindingB = Util.create(Hello.class, "b", HelloA.class);
        List<BindingConfig> list = new ArrayList<>();
        assertTrue(list.add(bindingA));
        assertTrue(list.contains(bindingB));

        // type implementation name
        bindingA = Util.create(Hello.class, "a", HelloA.class, null, "a");
        bindingB = Util.create(Hello.class, "b", HelloA.class, null, "a");
        list = new ArrayList<>();
        assertTrue(list.add(bindingA));
        assertTrue(list.contains(bindingB));

        bindingB = Util.create(Hello.class, "b", HelloA.class, null, "b");
        assertFalse(list.contains(bindingB));
    }

}