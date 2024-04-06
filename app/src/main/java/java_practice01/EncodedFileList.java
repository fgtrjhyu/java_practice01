package java_practice01;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import org.apache.commons.collections4.list.AbstractListDecorator;

/**
 * The EncodedFileList class is a decorator for a list of EncodedFile objects.
 * It extends the AbstractListDecorator class and provides additional
 * functionality
 * for collecting encoded files and finding the first valid zip file.
 */
public class EncodedFileList extends AbstractListDecorator<EncodedFile> {

    private static final String MSG_ERR = "Failed to open zip file: %s by %s";

    /**
     * The logger used for logging messages in the EncodedFileList class.
     */
    private static final Logger LOGGER = Logger.getLogger(EncodedFileList.class.getName());

    /**
     * Represents a list of encoded files.
     */
    public EncodedFileList(List<EncodedFile> list) {
        super(list);
    }

    /**
     * Collects the encoded files from the given file using the specified charsets.
     * 
     * @param files    the files to be encoded
     * @param charsets the charsets to use for encoding the file
     * @return the updated EncodedFileList object
     * @throws NullPointerException if either file or charsets is null
     */
    public EncodedFileList collect(Iterable<File> files, Iterable<Charset> charsets) {
        Objects.requireNonNull(files, "file is null");
        Objects.requireNonNull(charsets, "charsets is null");
        for (File file : files) {
            Objects.requireNonNull(file, "file is null");
            for (Charset charset : charsets) {
                Objects.requireNonNull(charset, "charset is null");
                add(new EncodedFile(file, charset));
            }
        }
        return this;
    }

    /**
     * Represents a collection of encoded files
     */
    public EncodedFileList collectFile(File file, Charset... charsets) {
        Objects.requireNonNull(file, "file is null");
        Objects.requireNonNull(charsets, "charsets is null");
        if (charsets.length == 0) {
            throw new IllegalArgumentException("charsets is empty");
        }
        return collect(Arrays.asList(file), Arrays.asList(charsets));
    }

    /**
     * Represents a list of encoded files.
     */
    public EncodedFileList collectCharset(Charset charset, File... files) {
        Objects.requireNonNull(charset, "charset is null");
        Objects.requireNonNull(files, "files is null");
        if (files.length == 0) {
            throw new IllegalArgumentException("files is empty");
        }
        return collect(Arrays.asList(files), Arrays.asList(charset));
    }

    /**
     * Finds the first valid zip file in the list of encoded files.
     *
     * @return An Optional containing the first valid EncodedFile as a zip file, or
     *         an empty Optional if no valid zip file is found.
     */
    public Optional<EncodedFile> findFirstValidZipFile() {
        for (EncodedFile encodedFile : decorated()) {
            // Try to open the zip file and return the encoded file if successful
            try (ZipFile zipFile = encodedFile.zipFile()) {
                if (encodedFile.validateEntryNames(zipFile)) {
                    return Optional.of(encodedFile);
                }
            } catch (IOException e) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, String.format(MSG_ERR, encodedFile,
                            encodedFile.charset()), e);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Represents a list of encoded files.
     */
    public EncodedFileList availables() {
        EncodedFileList result = new EncodedFileList(new LinkedList<EncodedFile>());
        for (EncodedFile encodedFile : decorated()) {
            try (ZipFile zipFile = encodedFile.zipFile()) {
                if (encodedFile.validateEntryNames(zipFile)) {
                    result.add(encodedFile);
                } else {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, String.format(MSG_ERR, encodedFile,
                                encodedFile.charset()));
                    }
                }
            } catch (IOException e) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, String.format(MSG_ERR, encodedFile,
                            encodedFile.charset()), e);
                }
            }
        }
        return result;
    }

}
