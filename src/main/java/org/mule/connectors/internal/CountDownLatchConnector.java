package org.mule.connectors.internal;

import org.mule.connectors.internal.function.CountDownLatchFunction;
import org.mule.connectors.internal.operations.CountDownLatchOperations;
import org.mule.runtime.extension.api.annotation.ExpressionFunctions;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;

@Extension(
    name = "Countdown Latch"
)
@Operations(CountDownLatchOperations.class)
@ErrorTypes(CountDownLatchErrors.class)
@ExpressionFunctions(CountDownLatchFunction.class)
public class CountDownLatchConnector {
}
