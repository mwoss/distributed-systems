package DistributedMap;

public interface SimplStringMap {
    boolean containsKey(String key);

    String get(String key);

    String put(String key, String value) throws Exception;

    String remove(String key) throws Exception;
}
