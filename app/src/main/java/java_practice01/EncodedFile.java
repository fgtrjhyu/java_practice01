package java_practice01;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.zip.ZipFile;

/**
 * Represents an encoded file with a specified charset.
 */
public record EncodedFile(File file, Charset charset) {
    /**
     * Constructs a new EncodedFile object.
     *
     * @param file    the file to be encoded
     * @param charset the character set used for encoding
     * @throws NullPointerException if either file or charset is null
     */
    public EncodedFile {
        Objects.requireNonNull(file, "file is null");
        Objects.requireNonNull(charset, "charset is null");
    }

    /**
     * Creates a new ZipFile object using the specified file and charset.
     *
     * @return a new ZipFile object
     * @throws IOException if an I/O error occurs
     */
    public ZipFile zipFile() throws IOException {
        return new ZipFile(file, charset);
    }
}