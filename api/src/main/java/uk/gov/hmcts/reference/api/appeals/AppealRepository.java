package uk.gov.hmcts.reference.api.appeals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class AppealRepository {
    private final AtomicInteger idSequence = new AtomicInteger();
    private final ConcurrentHashMap<Integer, Appeal> store = new ConcurrentHashMap<>();

    public Appeal createOrUpdate(Appeal appeal) {
        Integer id = appeal.getId() != null ? appeal.getId() : idSequence.incrementAndGet();
        store.put(id, appeal.withId(id));
        return store.get(id);
    }

    public Appeal retrieve(Integer id) {
        Appeal appeal = store.get(id);

        if (appeal == null) {
            throw new AppealNotFoundException();
        }

        return appeal;
    }
}
