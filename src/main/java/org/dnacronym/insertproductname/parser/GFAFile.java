package org.dnacronym.insertproductname.parser;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.parser.factories.AssemblyParserFactory;
import org.dnacronym.insertproductname.parser.factories.GFAParserFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Represents a GFA file with its contents and meta data.
 */
public class GFAFile {
    private String fileName;
    private String contents;
    private GFAParser gfaParser;
    private AssemblyParser assemblyParser;


    /**
     * Constructs a new {@code GFAFile}.
     *
     * @param fileName the name of the GFA file
     * @param contents the contents of the GFA file represented as a {@code String}
     */
    public GFAFile(final String fileName, final String contents) {
        this.fileName = fileName;
        this.contents = contents;

        gfaParser = GFAParserFactory.getInstance();
        assemblyParser = AssemblyParserFactory.getInstance();
    }


    /**
     * Reads a GFA file into memory and constructs a {@code GFAFile} object.
     * Is exposed as a named constructor to ease the construction of GFA file object.
     *
     * @param fileName the name of the GFA file
     * @return a {@code GFAFile} object.
     * @throws IOException if the given file name cannot be read
     */
    public static GFAFile read(final String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);

        return new GFAFile(fileName, content);
    }

    /**
     * Parses the GFA file into a {@code SequenceGraph}.
     *
     * @return a {@code SequenceGraph} based on the contents of the GFA file
     * @throws ParseException if the file content is not GFA-compliant
     */
    public final SequenceGraph parse() throws ParseException {
        return assemblyParser.parse(gfaParser.parse(contents));
    }

    /**
     * Get the name of the GFA file.
     *
     * @return the name of the GFA file.
     */
    public final String getFileName() {
        return fileName;
    }

    /**
     * Get the contents of the GFA file.
     *
     * @return the contents of the GFA file.
     */
    public final String getContents() {
        return contents;
    }
}
