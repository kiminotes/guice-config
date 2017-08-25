package guice.config;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a> 2017-08-21
 */
public interface ExtensionFactory {

    /**
     * 获取指定名称的扩展的实现.
     *
     * @param type class type
     * @param name extension name
     * @return extension instance if exists
     * @throws IllegalStateException if extension does not exists
     * @throws RuntimeException if there was a failure when create extension
     */
    <T> T getExtension(Class<T> type, String name);

    /**
     * 返回是否包含指定名称的扩展实现.
     *
     * @param type class type
     * @param name extension name
     * @return true if exists
     */
    <T> boolean containsExtension(Class<T> type, String name);

}
