package org.mule.connectors.internal.connection;

import static com.github.benmanes.caffeine.cache.Caffeine.newBuilder;

import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountDownLatchConnection implements Initialisable, Disposable {

    private Cache<String, Latch> latches = newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();

    private Logger LOGGER = LoggerFactory.getLogger(CountDownLatchConnection.class);

    @Inject
    SchedulerService schedulerService;

    private Scheduler ioScheduler;

    public CountDownLatchConnection() {
    }


    public String init(int number, String correlationId) {
        LOGGER.debug("Created new CountDownLatch. ID: {}  Count: {}", correlationId, number);
        latches.put(correlationId, new Latch(number));
        return correlationId;
    }

    public void countDown(String id) {
        Latch latch = latches.getIfPresent(id);
        if (latch != null) {
            synchronized (latch) {
                LOGGER.debug("Counting Down. ID: {}", id);
                latch.count--;
                if (latch.count <= 0) {
                    LOGGER.debug("Reached 0, calling callbacks. ID: {}", id);
                    latch.callbacks.forEach(c -> c.success(Result.<Void, Void>builder().build()));
                }
            }
        } else {
            throw new RuntimeException("ID doesn't exist");
        }
    }

    public void await(String id, CompletionCallback<Void, Void> callback) {
        Latch latch = latches.getIfPresent(id);
        if (latch != null) {
            synchronized (latch) {
                LOGGER.debug("Await for CountDownLatch. ID: {}", id);
                if (latch.count > 0) {
                    LOGGER.debug("Registering callback. ID: {}", id);
                    latch.callbacks.add(callback);
                } else {
                    LOGGER.debug("CountDownLatch is open, calling callback. ID: {}", id);
                    callback.success(Result.<Void, Void>builder().build());
                }
            }
        } else {
            throw new RuntimeException("ID doesn't exist");
        }
    }

    @Override
    public void dispose() {
        this.ioScheduler.stop();
    }

    @Override
    public void initialise() {
        this.ioScheduler = schedulerService.ioScheduler();
    }

    public void save(String id, String key, TypedValue<Object> value) {
        Latch latch = latches.getIfPresent(id);
        if (latch != null) {
          latch.withVar(key, value);
        } else {
            throw new RuntimeException("ID doesn't exist");
        }
    }

    public Integer getCount(String id) {
        Latch latch = latches.getIfPresent(id);
        if (latch != null) {
            return latch.getCount();
        } else {
            throw new RuntimeException("ID doesn't exist");
        }
    }

    private static class Latch {

        private int count;
        private List<CompletionCallback<Void, Void>> callbacks = new ArrayList<>();
        private Map<String, Object> variables = new HashMap<>();

        Latch(int count) {
            this.count = count;
        }

        public void withVar(String key, Object value) {
            variables.put(key, value);
        }

        public Map<String, Object> getVariables() {
            return variables;
        }

        public int getCount() {
            return count;
        }
    }
}
