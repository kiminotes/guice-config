package guice.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

    final Map<String, List<String>> selections = new HashMap<>();

    public BindingSelector(Properties properties) {
        Preconditions.checkNotNull(properties);

        for (String key : properties.stringPropertyNames()) {
            if (key.length() > PREFIX.length()
                && key.startsWith(PREFIX)) {
                final String value = properties.getProperty(key);
                if (Strings.isNullOrEmpty(value)) {
                    continue;
                }
                String[] items = SEPARATOR.split(value);
                for (String item : items) {
                    Util.addToValue(selections, key.substring(PREFIX.length()), item);
                }
            }
        }
    }

    public List<BindingConfig> select(List<BindingConfig> bindings) {
        final List<BindingConfig> result = new ArrayList<>();
        final Map<String, Selection> map = new HashMap<>();
        final Map<Class<?>, List<BindingConfig>> resultMap = new HashMap<>();

        for (int i = 0; i < bindings.size(); i++) {
            BindingConfig binding = bindings.get(i);
            final String typeName = binding.getType().getName();
            final List<String> selectionIds = selections.get(typeName);
            if (selectionIds == null
                || selectionIds.contains(binding.getId())) {
                Util.addToValue(resultMap, binding.getType(), binding);
            }

            putSelection(map, selectionIds, binding);
        }

        showSelection(map);
        // 确保 binding 的顺序与 property 中设置的一致
        for (Map.Entry<Class<?>, List<BindingConfig>> entry : resultMap.entrySet()) {
            final List<String> ids = selections.get(entry.getKey().getName());
            if (ids != null) {
                List<BindingConfig> value = entry.getValue();
                for (int i = 0; i < ids.size(); i++) {
                    for (int j = 0; j < value.size(); j++) {
                        if (ids.get(i).equals(value.get(j).getId())) {
                            result.add(value.get(j));
                        }
                    }
                }
            } else {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    void putSelection(final Map<String, Selection> map, List<String> selectionIds, BindingConfig binding) {
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
