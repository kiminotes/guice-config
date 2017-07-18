package guice.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-26
 */
class BindingSelector {

    static final String  PREFIX    = "binding.selector.";
    static final Pattern SEPARATOR = Pattern.compile(",");

    final Map<String, Set<String>> selections = new HashMap<>();

    public BindingSelector(Properties properties) {
        Preconditions.checkNotNull(properties);

        for (String key : properties.stringPropertyNames()) {
            if (key.length() > PREFIX.length()
                && key.startsWith(PREFIX)) {
                addSelections(key.substring(PREFIX.length()),
                    properties.getProperty(key),
                    selections);
            }
        }
    }

    void addSelections(String typeName, String value, Map<String, Set<String>> map) {
        String[] names = SEPARATOR.split(value);
        Set<String> set = map.get(typeName);
        if (set == null) {
            set = new HashSet<>();
            map.put(typeName, set);
        }
        set.addAll(Arrays.asList(names));
    }

    public List<BindingConfig> select(List<BindingConfig> bindings) {
        final List<BindingConfig> result = new ArrayList<>();

        for (int i = 0; i < bindings.size(); i++) {
            BindingConfig binding = bindings.get(i);
            final String typeName = binding.getType().getName();
            final Set<String> selectionIds = selections.get(typeName);
            if (selectionIds == null
                || selectionIds.contains(binding.getId())) {
                result.add(binding);
            }
        }

        return result;
    }

}
