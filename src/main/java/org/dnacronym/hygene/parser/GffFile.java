package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.Annotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


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
     * @return contents of the GFF file
     * @throws ParseException if the given file name cannot be read
     */
    public BufferedReader readFile() throws ParseException {
        try {
            return Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new ParseException("File '" + fileName + "' cannot be read. ", e);
        }
    }

    /**
     * Parses the GFF file.
     *
     * @return a {@link String} list representing the thing
     * @throws ParseException if the file content is not GFF-compliant
     */
    public List<Annotation> parse() throws ParseException {
        return gffParser.parse(this);
    }
}
