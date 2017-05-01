package org.dnacronym.insertproductname.parser.factories;

import org.dnacronym.insertproductname.parser.AssemblyParser;


/**
 * Constructs {@code AssemblyParser} objects.
 */
public final class AssemblyParserFactory {
    private static AssemblyParser instance;


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
     * Gets an instance of {@code AssemblyParser}. Constructs a new one if necessary.
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
