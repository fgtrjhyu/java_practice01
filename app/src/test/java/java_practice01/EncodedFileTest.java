package java_practice01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class contains unit tests for the EncodedFile class.
 */
public class EncodedFileTest {
    private File file;
    private Charset shiftJis;
    private Charset eucJp;
    private Charset utf8;
    private Charset iso2022jp;

    @BeforeEach
    void setUp() {
        file = new File("src\\test\\resources\\test.zip");
        shiftJis = Charset.forName("Shift_JIS");
        eucJp = Charset.forName("euc-jp");
        utf8 = Charset.forName("UTF-8");
        iso2022jp = Charset.forName("ISO-2022-JP");
    }

    @Test
    void testZipFileWithShiftJIS() throws IOException {
        EncodedFile encodedFile = new EncodedFile(file, shiftJis);
        assertEquals(file, encodedFile.file());
        assertEquals(shiftJis, encodedFile.charset());
        // Call the zipFile() method
        try (var zipFile = encodedFile.zipFile()) {
            assertNotNull(zipFile);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testZipFileWithEucJp() throws IOException {
        EncodedFile encodedFile = new EncodedFile(file, eucJp);
        assertEquals(file, encodedFile.file());
        assertEquals(eucJp, encodedFile.charset());
        assertThrows(IOException.class, () -> encodedFile.zipFile());
    }

    @Test
    void testZipFileWithUtf9() throws IOException {
        EncodedFile encodedFile = new EncodedFile(file, utf8);
        assertEquals(file, encodedFile.file());
        assertEquals(utf8, encodedFile.charset());
        assertThrows(IOException.class, () -> encodedFile.zipFile());
    }

    @Test
    void testZipFileWithIso2022Jp() throws IOException {
        EncodedFile encodedFile = new EncodedFile(file, iso2022jp);
        assertEquals(file, encodedFile.file());
        assertEquals(iso2022jp, encodedFile.charset());
        assertThrows(IOException.class, () -> encodedFile.zipFile());
    }

    @Test
    void testConstructorNullFile() {
        // Assert that NullPointerException is thrown when file is null
        assertThrows(NullPointerException.class, () -> new EncodedFile(null, Charset.defaultCharset()));
    }

    @Test
    void testConstructorNullCharset() {
        // Create a temporary file
        assertThrows(NullPointerException.class, () -> new EncodedFile(file, null));
    }
}