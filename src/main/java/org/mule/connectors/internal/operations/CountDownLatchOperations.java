package org.mule.connectors.internal.operations;

import org.mule.connectors.internal.connection.CountDownLatchConnection;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Ignore;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;

import javax.inject.Inject;

@Throws(OperationsErrorTypeProvider.class)
public class CountDownLatchOperations {

    @Inject
    CountDownLatchConnection connection;

    /**
     * Inits a new countdown latch. By default uses the current Correlation ID as the ID to register the new latch.
     *
     * @param number the number of times the Countdown operation must be invoked before flows execution can pass through the Await operation.
     * @param id     the ID to identify the Countdown Latch between operations.
     */
    public void init(@Optional(defaultValue = "1") int number, @Optional(defaultValue = "#[correlationId]") String id) {
        connection.init(number, id);
    }

    /**
     * Decrements the count of the latch, releasing all waiting flows if the count reaches zero.
     * <p>
     * If the current count is greater than zero then it is decremented.
     * If the current count equals zero then nothing happens.
     *
     * @param id ID of the Countdown Latch to use
     */
    public void countdown(@Optional(defaultValue = "#[correlationId]") String id) {
        connection.countDown(id);
    }

    @Ignore
    public void save(@Optional(defaultValue = "#[correlationId]") String id, String key, @Optional(defaultValue = "#[payload]") TypedValue<Object> value) {
        connection.save(id, key, value);
    }

    /**
     * Causes the current flow to wait until the latch has counted down to
     * zero.
     *
     * @param id ID of the Countdown Latch to use
     */
    public void await(@Optional(defaultValue = "#[correlationId]") String id, CompletionCallback<Void, Void> callback) {
        connection.await(id, callback);
    }

}
