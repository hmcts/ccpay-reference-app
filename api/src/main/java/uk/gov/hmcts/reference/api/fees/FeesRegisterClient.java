package uk.gov.hmcts.reference.api.fees;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FeesRegisterClient {
    private final Map<String, Integer> store = ImmutableMap.of(
            "X001", 50000,
            "X002", 55000,
            "X003", 60000
    );

    public Integer retrieve(String id) {
        return store.get(id);
    }
}
