package guice.config;

import java.io.Serializable;

/**
 * @author <a href="mailto:kiminotes.lv@gmail.com">kimi</a> 2017-06-25
 */
public class BindingConfig implements Serializable {

    private static final long serialVersionUID = -8438469304064399866L;

    Class<?> type;
    Class<?> implementation;
    String   id;
    String   scope;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getImplementation() {
        return implementation;
    }

    public void setImplementation(Class<?> implementation) {
        this.implementation = implementation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BindingConfig config = (BindingConfig) o;

        if (type != null ? !type.equals(config.type) : config.type != null) return false;
        return implementation != null ? implementation.equals(config.implementation) : config.implementation == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (implementation != null ? implementation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Binding [" + type.getName() + " -> " + implementation.getName() + "]";
    }
}
