package java_practice01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.zip.ZipException;

import org.junit.jupiter.api.Test;

public class EncodedZipFileTest {
    @Test
    void testOf() throws IOException, ZipException {
        File file = new File("src\\test\\resources\\test.zip");
        Charset charset = Charset.forName("Shift_JIS");

        EncodedZipFile encodedZipFile = EncodedZipFile.of(charset, file);

        assertNotNull(encodedZipFile);
        assertEquals(file, encodedZipFile.file());
        assertEquals(charset, encodedZipFile.charset());
        assertNotNull(encodedZipFile.zipFile());
    }

    @Test
    void testOfOptional() {
        File file = new File("src\\test\\resources\\test.zip");
        Charset charset = Charset.forName("Shift_JIS");

        Optional<EncodedZipFile> optionalEncodedZipFile = EncodedZipFile.ofOptional(charset, file);

        assertTrue(optionalEncodedZipFile.isPresent());
        EncodedZipFile encodedZipFile = optionalEncodedZipFile.get();
        assertEquals(file, encodedZipFile.file());
        assertEquals(charset, encodedZipFile.charset());
        assertNotNull(encodedZipFile.zipFile());
    }

    @Test
    void testFindFirstOfOptional() {
        File file = new File("src\\test\\resources\\test.zip");
        Charset charset = Charset.forName("Shift_JIS");
        String[] charsets = { "euc-jp", "UTF-8", "Shift_JIS", "ISO-8859-1" };

        Optional<EncodedZipFile> optionalEncodedZipFile = EncodedZipFile.findFirstOfOptional(file, charsets);

        assertTrue(optionalEncodedZipFile.isPresent());
        EncodedZipFile encodedZipFile = optionalEncodedZipFile.get();
        assertEquals(file, encodedZipFile.file());
        assertEquals(charset, encodedZipFile.charset());
        assertNotNull(encodedZipFile.zipFile());
    }
}