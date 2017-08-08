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
    String   name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // type, implementation, name 参与 equals 运算
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BindingConfig that = (BindingConfig) o;

        if (!type.equals(that.type)) return false;
        if (!implementation.equals(that.implementation)) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + implementation.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Binding [" + type.getName() + " -> " + implementation.getName() + "]";
    }
}
