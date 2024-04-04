package java_practice01;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.list.AbstractListDecorator;

import java.util.zip.ZipFile;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Optional;

/**
 * The EncodedFileList class is a decorator for a list of EncodedFile objects.
 * It extends the AbstractListDecorator class and provides additional
 * functionality
 * for collecting encoded files and finding the first valid zip file.
 */
public class EncodedFileList extends AbstractListDecorator<EncodedFile> {

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
     * @param file     the file to collect encoded files from
     * @param charsets the charsets to use for encoding the file
     * @return the updated EncodedFileList object
     * @throws NullPointerException if either file or charsets is null
     */
    public EncodedFileList collect(File file, Iterable<Charset> charsets) {
        Objects.requireNonNull(file, "file is null");
        Objects.requireNonNull(charsets, "charsets is null");
        for (Charset charset : charsets) {
            Objects.requireNonNull(charset, "charset is null");
            add(new EncodedFile(file, charset));
        }
        return this;
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
                return Optional.of(encodedFile);
            } catch (IOException e) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, String.format("Failed to open zip file: %s by %s", encodedFile.file(),
                            encodedFile.charset()), e);
                }
            }
        }
        return Optional.empty();
    }

}
