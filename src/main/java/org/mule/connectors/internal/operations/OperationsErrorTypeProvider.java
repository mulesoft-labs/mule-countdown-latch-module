package org.mule.connectors.internal.operations;

import org.mule.connectors.internal.CountDownLatchErrors;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

public class OperationsErrorTypeProvider implements ErrorTypeProvider {
    @Override
    public Set<ErrorTypeDefinition> getErrorTypes() {
        HashSet<ErrorTypeDefinition> errors = new HashSet<>();
        errors.add(CountDownLatchErrors.INVALID_ID);
        return errors;
    }
}
