package mitasov.calc;

import java.util.*;

public class Constants {
    private boolean isModified = true;
    private Map<String, Double> constMap;

    Constants() {
        this.constMap = new LinkedHashMap<>();
    }

    Constants add(String name) {
        constMap.put(name, 0D);
        return this;
    }

    void resetModified() {
        isModified = false;
    }

    boolean isModified() {
        return isModified;
    }

    boolean isEmpty() {
        return constMap.isEmpty();
    }

    public double get(String name) {
        return constMap.get(name);
    }

    public Constants put(String name, double value) throws Exception {
        if (!constMap.containsKey(name)) {
            throw new IllegalArgumentException("No such constant in set");
        }

        isModified = true;
        constMap.put(name, value);
        return this;
    }

    public Set<String> names() {
        return Collections.unmodifiableSet(constMap.keySet());
    }

    public Set<Map.Entry<String, Double>> entrySet() {
        return Collections.unmodifiableSet(constMap.entrySet());
    }

    public Collection<Double> values() {
        return Collections.unmodifiableCollection(constMap.values());
    }
}