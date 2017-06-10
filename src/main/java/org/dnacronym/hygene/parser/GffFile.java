package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.GeneAnnotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Parses a GFF file.
 */
public final class GffFile {
    private final String fileName;
    private final GffParser gffParser;


    /**
     * Creates an instance of a {@link GffFile}.
     *
     * @param fileName the name of the GFF file
     */
    public GffFile(final String fileName) {
        this.fileName = fileName;

        this.gffParser = new GffParser();
    }


    /**
     * Reads a GFF file into memory and gives its contents as a {@link BufferedReader}.
     *
     * @return the contents of the GFF file
     * @throws ParseException if the given file name cannot be read
     */
    public BufferedReader readFile() throws ParseException {
        try {
            return Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new ParseException("File '" + fileName + "' cannot be read.", e);
        }
    }

    /**
     * Parses this GFF file.
     *
     * @return a {@link GeneAnnotation} representing the file
     * @throws ParseException if the file content is not GFF-compliant
     */
    public GeneAnnotation parse() throws ParseException {
        return gffParser.parse(this);
    }
}
