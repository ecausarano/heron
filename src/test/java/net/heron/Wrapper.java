package net.heron;

import java.util.HashMap;
import java.util.Map;

/**
* This file is part of heron
*/
public class Wrapper {
    private Map<String, String> map = new HashMap<>();

    public String put(String key, String value) {
        return map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }
}
