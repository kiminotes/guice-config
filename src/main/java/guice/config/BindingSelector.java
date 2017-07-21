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
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-26
 */
class BindingSelector {

    static final Logger LOG = LoggerFactory.getLogger(BindingSelector.class);

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
        final Map<String, Selection> map = new HashMap<>();

        for (int i = 0; i < bindings.size(); i++) {
            BindingConfig binding = bindings.get(i);
            final String typeName = binding.getType().getName();
            final Set<String> selectionIds = selections.get(typeName);
            if (selectionIds == null
                || selectionIds.contains(binding.getId())) {
                result.add(binding);
            }

            putSelection(map, selectionIds, binding);
        }

        showSelection(map);
        return result;
    }

    void putSelection(final Map<String, Selection> map, Set<String> selectionIds, BindingConfig binding) {
        final String typeName = binding.getType().getName();
        if (selectionIds != null) {
            Selection s = map.get(typeName);
            if (s == null) {
                s = new Selection();
                map.put(typeName, s);
            }
            if (selectionIds.contains(binding.getId())) {
                s.included.add(binding);
            } else {
                s.excluded.add(binding);
            }
        }
    }

    void showSelection(final Map<String, Selection> map) {
        if (Boolean.getBoolean("show.selections")) {
            System.out.println(buildSelectionMessage(map));
        }
    }

    String buildSelectionMessage(final Map<String, Selection> map) {
        final StringBuilder buf = new StringBuilder(128);
        final int indent = 2;
        for (Map.Entry<String, Selection> entry : map.entrySet()) {
            Util.appendIndent(buf, indent).append(entry.getKey()).append(Util.NL);

            final String include = Util.implementationsString(entry.getValue().included);
            if (!Strings.isNullOrEmpty(include)) {
                Util.appendIndent(buf, indent * 2).append("include: " + include ).append(Util.NL);
            }

            final String exclude = Util.implementationsString(entry.getValue().excluded);
            if (!Strings.isNullOrEmpty(exclude)) {
                Util.appendIndent(buf, indent * 2).append("exclude: " + exclude).append(Util.NL);
            }
        }
        return buf.toString();
    }

    class Selection {
        final List<BindingConfig> included = new ArrayList<>();
        final List<BindingConfig> excluded = new ArrayList<>();
    }

}
