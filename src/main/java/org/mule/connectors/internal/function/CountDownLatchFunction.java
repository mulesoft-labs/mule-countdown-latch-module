package org.mule.connectors.internal.function;

import org.mule.connectors.internal.connection.CountDownLatchConnection;
import org.mule.runtime.extension.api.annotation.param.Optional;

import javax.inject.Inject;

public class CountDownLatchFunction {

    @Inject
    CountDownLatchConnection connection;

    public Integer getCount(@Optional String id) {
        return connection.getCount(id);
    }
}
