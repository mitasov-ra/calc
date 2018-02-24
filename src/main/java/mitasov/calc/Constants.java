package mitasov.calc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Constants {
    private boolean isModified = true;
    private Map<String, Double> constMap;

    Constants() {
        this.constMap = new HashMap<>();
    }

    public Constants add(String name, double val) {
        constMap.put(name, val);
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

    public Constants set(String name, double value) throws Exception {
        if (constMap.containsKey(name)) {
            isModified = true;
            constMap.put(name, value);
            return this;
        } else {
            throw new Exception("No such constant in set");
        }
    }

    public Set<String> getNames() {
        return constMap.keySet();
    }
}
