package java_practice01;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Represents an encoded file with a specified charset.
 */
public record EncodedFile(File file, Charset charset) {

    private static final String ERROR_MESSAGE_MALFORMED_ENTRY_NAME = "Entry name is not correctly interpreted %s by %s charset";

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(EncodedFile.class.getName());

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
     * This method is used to write data to a file as a stream of bytes.
     * An instance of this class represents an output stream for writing bytes to a
     * file. The caller is responsible for closing the stream.
     *
     * @return FileOutputStream for the file
     *         It is recommended to use this method within a try-with-resources
     *         statement to ensure the stream is closed properly.
     * @throws IOException if an I/O error occurs
     */
    public FileOutputStream openOutputStream() throws IOException {
        return new FileOutputStream(file);
    }

    /**
     * An OutputStreamWriter is a bridge from character streams to byte streams:
     * Characters written to it are encoded into bytes using a specified charset.
     * The charset that it uses may be specified by name or may be given explicitly,
     * or the platform's default charset may be accepted.
     * Each invocation of a write() method causes the encoding converter to be
     * invoked on the given character(s).
     * The resulting bytes are accumulated in a buffer before being written to the
     * underlying output stream. The caller is responsible for closing the stream.
     * 
     * @return OutputStreamWriter for the file
     *         It is recommended to use this method within a try-with-resources
     *         statement to ensure the stream is closed properly.
     * 
     * @throws IOException if an I/O error occurs
     */
    public OutputStreamWriter openOutputStreamWriter() throws IOException {
        return new OutputStreamWriter(openOutputStream(), charset);
    }

    /**
     * This method is used to write data to a zip file as a stream of bytes.
     * An instance of this class represents an output stream for writing bytes to a
     * file. The caller is responsible for closing the stream.
     *
     * @return ZipOutputStream for the file
     *         It is recommended to use this method within a try-with-resources
     *         statement to ensure the stream is closed properly.
     * @throws IOException if an I/O error occurs
     */
    public ZipOutputStream openZipOutputStream() throws IOException {
        return new ZipOutputStream(openOutputStream(), charset);
    }

    /**
     * This method is used to read data to a file as a stream of bytes.
     * An instance of this class represents an input stream for reading bytes to a
     * file. The caller is responsible for closing the stream.
     *
     * @return FileOutputStream for the file
     *         It is recommended to use this method within a try-with-resources
     *         statement to ensure the stream is closed properly.
     * @throws IOException if an I/O error occurs
     */
    public FileInputStream openInputStream() throws IOException {
        return new FileInputStream(file);
    }

    /**
     * An InputStreamReader is a bridge from character streams to byte streams:
     * Characters read to it are encoded into bytes using a specified charset.
     * The charset that it uses may be specified by name or may be given explicitly,
     * or the platform's default charset may be accepted.
     * Each invocation of a read() method causes the encoding converter to be
     * invoked on the given character(s).
     * The resulting bytes are accumulated in a buffer before being written to the
     * underlying input stream. The caller is responsible for closing the stream.
     * 
     * @return InputStreamReader for the file
     *         It is recommended to use this method within a try-with-resources
     *         statement to ensure the stream is closed properly.
     * @throws IOException if an I/O error occurs
     */
    public InputStreamReader openInputStreamReader() throws IOException {
        return new InputStreamReader(openInputStream(), charset);
    }

    /**
     * This method is used to read data to a zip file as a stream of bytes.
     * An instance of this class represents an read stream for reading bytes to a
     * file. The caller is responsible for closing the stream.
     *
     * @return ZipInputStream for the file
     *         It is recommended to use this method within a try-with-resources
     *         statement to ensure the stream is closed properly.
     * @throws IOException if an I/O error occurs
     */
    public ZipInputStream openZipInputStream() throws IOException {
        return new ZipInputStream(openInputStream(), charset);
    }

    /**
     * Creates a new ZipFile object using the specified file and charset.
     *
     * @return a new ZipFile object It is recommended to use this method within a
     *         try-with-resources statement to ensure the stream is closed properly.
     * @throws IOException if an I/O error occurs
     */
    public ZipFile openZipFile() throws IOException {
        return new ZipFile(file, charset);
    }

    /**
     * Re-encodes a string that might be broken using the specified charset.
     * Even though the same charset is used for re-encoding, due to the nature of
     * Java's String,
     * it's possible that the original string was not correctly formed, resulting in
     * a different output after re-encoding.
     *
     * @param potentiallyMalformedString the string that might be broken or
     *                                   malformed
     * @return the re-encoded string maybe a different string.
     * @throws NullPointerException if the input string is null
     */
    private String reencodePotentiallyMalformedString(String potentiallyMalformedString) {
        Objects.requireNonNull(potentiallyMalformedString, "potentiallyMalformedString is null");
        return new String(potentiallyMalformedString.getBytes(charset), charset);
    }

    /**
     * This method determines whether the string is correctly interpreted. If the
     * string given was decoded using the same character set, encoded and returned
     * to the same string, then the string was interpreted correctly. However, if it
     * changed to another string, the original string was generated under an
     * incorrect interpretation.
     *
     * @param potentiallyMalformedString the string that might be broken or
     *                                   malformed
     * @return true if the string is correctly interpreted, false otherwise
     * @throws NullPointerException if the input string is null
     */
    private boolean validateStringInterpretation(String potentiallyMalformedString) {
        Objects.requireNonNull(potentiallyMalformedString, "potentiallyMalformedString is null");
        return potentiallyMalformedString.equals(reencodePotentiallyMalformedString(potentiallyMalformedString));
    }

    /**
     * Validates the names of entries in a given ZipFile.
     *
     * @param zipFile the ZipFile to validate
     * @return true if all entry names are valid, false otherwise
     */
    public boolean validateEntryNames(ZipFile zipFile) {
        for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
            String entryName = entries.nextElement().getName();
            if (!validateStringInterpretation(entryName)) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning(String.format(ERROR_MESSAGE_MALFORMED_ENTRY_NAME, entryName, charset));
                }
                return false;
            }
        }
        return true;
    }
}