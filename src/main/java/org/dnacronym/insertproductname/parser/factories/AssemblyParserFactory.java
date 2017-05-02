package org.dnacronym.insertproductname.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.insertproductname.parser.AssemblyParser;


/**
 * Constructs {@code AssemblyParser} objects.
 */
public final class AssemblyParserFactory {
    private static @MonotonicNonNull AssemblyParser instance;


    /**
     * Makes class non instantiable.
     */
    private AssemblyParserFactory() {
    }


    /**
     * Sets a custom instance of {@code AssemblyParser}.
     *
     * @param assemblyParser custom instance of {@code AssemblyParser}
     */
    public static void setInstance(final AssemblyParser assemblyParser) {
        instance = assemblyParser;
    }

    /**
     * Returns a new instance of {@code AssemblyParser} if no preferred instance is set.
     *
     * @return an instance of {@code AssemblyParser}
     */
    public static AssemblyParser getInstance() {
        if (instance == null) {
            return new AssemblyParser();
        }

        return instance;
    }
}
