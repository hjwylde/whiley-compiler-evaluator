package com.hjwylde.profiler.profiler;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class Task {

    private final String clazz;
    private final String method;

    public Task(String clazz, String method) {
        this.clazz = checkNotNull(clazz, "clazz cannot be null");
        this.method = checkNotNull(method, "method cannot be null");
    }

    public String getClazz() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return clazz + "$" + method;
    }
}
