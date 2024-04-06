package java_practice01;

import java.io.File;
import java.io.FileInputStream; // Import the FileInputStream class
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader; // Import the InputStreamReader class
import java.io.OutputStreamWriter; // Import the OutputStreamWriter class
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream; // Import the ZipInputStream class

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

    public FileOutputStream getOutputStream() throws IOException {
        return new FileOutputStream(file);
    }

    public OutputStreamWriter getOutputStreamWriter() throws IOException {
        return new OutputStreamWriter(getOutputStream(), charset);
    }

    public ZipOutputStream getZipOutputStream() throws IOException {
        return new ZipOutputStream(getOutputStream(), charset);
    }

    public FileInputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    public InputStreamReader getInputStreamReader() throws IOException {
        return new InputStreamReader(getInputStream(), charset);
    }

    public ZipInputStream getZipInputStream() throws IOException {
        return new ZipInputStream(getInputStream(), charset);
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

    public String rebuildString(String s) {
        Objects.requireNonNull(s, "s is null");
        return new String(s.getBytes(charset()), charset());
    }

    public boolean validateString(String s) {
        Objects.requireNonNull(s, "s is null");
        return s.equals(rebuildString(s));
    }

    /**
     * Validates the names of entries in a given ZipFile.
     *
     * @param zipFile the ZipFile to validate
     * @return true if all entry names are valid, false otherwise
     */
    public boolean validateEntryNames(ZipFile zipFile) {
        boolean valid = true;
        for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
            valid &= validateString(entries.nextElement().getName());
        }
        return valid;
    }
}