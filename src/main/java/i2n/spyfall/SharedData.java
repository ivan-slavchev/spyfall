package i2n.spyfall;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SharedData {

    private Map<String, Long> sharedDataMap = new HashMap<>();

    public Map<String, Long> getSharedDataMap() {
        return sharedDataMap;
    }
}
