package guice.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import guice.config.demo.Hello;
import guice.config.demo.HelloA;
import guice.config.demo.HelloB;
import guice.config.demo.HelloC;
import guice.config.demo.HelloWorld;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-07-18
 */
public class BindingSelectorTest {

    BindingSelector selector;

    final BindingConfig       bindingA  = Util.create(Hello.class, "a", HelloA.class);
    final BindingConfig       bindingB  = Util.create(Hello.class, "b", HelloB.class);
    final BindingConfig       bindingC  = Util.create(Hello.class, "c", HelloC.class);
    final BindingConfig       bindingHW = Util.create(HelloWorld.class, "hw", HelloWorld.class);
    final List<BindingConfig> bindings  = new ArrayList<>(4);

    @Before
    public void before() throws Exception {
        bindings.clear();
        bindings.add(bindingA);
        bindings.add(bindingB);
        bindings.add(bindingC);
        bindings.add(bindingHW);
        System.setProperty("show.selections", Boolean.TRUE.toString());
    }

    @Test
    public void test_selectMatchOne() throws Exception {
        List<BindingConfig> selections;

        Properties properties = new Properties();
        buildSelectionProperty(properties, Hello.class, bindingA.getId());
        buildSelectionProperty(properties, HelloWorld.class, "xxxx");

        selector = new BindingSelector(properties);
        selections = selector.select(bindings);

        assertEquals(1, selections.size());
        assertEquals(Hello.class, selections.get(0).getType());
        assertEquals(HelloA.class, selections.get(0).getImplementation());
        assertEquals(bindingA.getId(), selections.get(0).getId());
    }

    @Test
    public void test_selectMatchMulti() throws Exception {
        Properties properties = new Properties();
        final String[] ids = new String[]{bindingC.getId(), bindingB.getId()};
        buildSelectionProperty(properties, Hello.class, ids);
        buildSelectionProperty(properties, HelloWorld.class, "xxxx");

        selector = new BindingSelector(properties);
        List<BindingConfig> selections = selector.select(bindings);

        assertEquals(2, selections.size());
        assertEquals(Hello.class, selections.get(0).getType());
        assertEquals(Hello.class, selections.get(1).getType());

        assertEquals(ids[0], selections.get(0).getId());
        assertEquals(ids[1], selections.get(1).getId());

        final Set<Class<?>> impls = new HashSet<>();
        impls.add(HelloC.class);
        impls.add(HelloB.class);
        assertTrue(impls.remove(selections.get(0).getImplementation()));
        assertTrue(impls.remove(selections.get(1).getImplementation()));
        assertTrue(impls.isEmpty());
    }

    @Test
    public void test_selectNotInSelections() throws Exception {
        Properties properties = new Properties();
        buildSelectionProperty(properties, Hello.class, "xxxx");
        selector = new BindingSelector(properties);
        List<BindingConfig> selections = selector.select(bindings);
        assertEquals(1, selections.size());
        assertEquals(bindingHW.getId(), selections.get(0).getId());
        assertEquals(HelloWorld.class, selections.get(0).getType());
        assertEquals(HelloWorld.class, selections.get(0).getImplementation());
    }

    void buildSelectionProperty(Properties properties, Class<?> cl, String... ids) {
        final String value = buildSelectionValue(BindingSelector.SEPARATOR.pattern(), ids);
        properties.setProperty(BindingSelector.PREFIX + cl.getName(), value);
    }

    String buildSelectionValue(String separator, String... ids) {
        final StringBuilder buf = new StringBuilder(32);
        for (String id : ids) {
            buf.append(id).append(separator);
        }
        if (buf.length() > separator.length()) {
            buf.setLength(buf.length() - separator.length());
        }
        return buf.toString();
    }

}