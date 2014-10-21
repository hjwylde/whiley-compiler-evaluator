package com.hjwylde.profiler.util;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.0
 */
public final class Functions {

    private Functions() {}

    public static <A, B> B reduce(Function2<B, A, B> function, B def, Iterable<A> elements) {
        B ret = def;
        for (A a : elements) {
            ret = function.apply(ret, a);
        }

        return ret;
    }
}
