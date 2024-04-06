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
import java.util.zip.ZipEntry;

/**
 * This class contains unit tests for the EncodedFile class.
 */
public class EncodedFileTest {
    private File file;

    private Charset shiftJis;
    private File shiftJisFile;

    private Charset utf8;
    private File utf8File;

    @BeforeEach
    void setUp() {
        file = new File("src\\test\\resources\\test.zip");
        shiftJis = Charset.forName("Shift_JIS");
        shiftJisFile = new File("src\\test\\resources\\testShiftJis.zip");
        utf8 = Charset.forName("UTF-8");
        utf8File = new File("src\\test\\resources\\testUtf8.zip");
    }

    @Test
    void testZipOutputStreamShiftJis() throws IOException {
        EncodedFile encodedFile = new EncodedFile(shiftJisFile, shiftJis);
        assertEquals(shiftJisFile, encodedFile.file());
        assertEquals(shiftJis, encodedFile.charset());
        // Call the zipOutputStream() method
        try (var stream = encodedFile.openZipOutputStream()) {
            ZipEntry entry = new ZipEntry("日本語エントリ");
            stream.putNextEntry(entry);
            stream.write("日本語".getBytes(encodedFile.charset()));
            stream.closeEntry();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        try (var stream = encodedFile.openZipInputStream()) {
            for (ZipEntry entry = stream.getNextEntry(); entry != null; entry = stream.getNextEntry()) {
                assertEquals("日本語エントリ", entry.getName());
                byte[] buffer = new byte[1024];
                int len = stream.read(buffer);
                assertEquals("日本語", new String(buffer, 0, len, encodedFile.charset()));
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testZipOutputStreamUtf8() throws IOException {
        EncodedFile encodedFile = new EncodedFile(utf8File, utf8);
        assertEquals(utf8File, encodedFile.file());
        assertEquals(utf8, encodedFile.charset());

        byte[] firstEntryNameBytes = new byte[] { (byte) 0x81, (byte) 0x40, (byte) 0x81 };
        String firstEntryName = new String(firstEntryNameBytes, utf8);
        String secondEntryName = "￭🧦";
        // Call the zipOutputStream() method
        try (var stream = encodedFile.openZipOutputStream()) {
            byte[] buffer;
            //
            ZipEntry firstEntry = new ZipEntry(firstEntryName);
            buffer = "ࠀ🦗🦓".getBytes(encodedFile.charset());
            firstEntry.setSize(buffer.length);
            stream.putNextEntry(firstEntry);
            stream.write(buffer);
            stream.closeEntry();
            //
            ZipEntry secondEntry = new ZipEntry(secondEntryName);
            buffer = "ࠀ🦗".getBytes(encodedFile.charset());
            secondEntry.setSize(buffer.length);
            stream.putNextEntry(secondEntry);
            stream.write(buffer);
            stream.closeEntry();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        try (var stream = encodedFile.openZipInputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            //
            ZipEntry firstEntry = stream.getNextEntry();
            assertEquals(firstEntryName, firstEntry.getName());
            len = stream.read(buffer);
            assertEquals("ࠀ🦗🦓", new String(buffer, 0, len, encodedFile.charset()));
            stream.closeEntry();
            //
            ZipEntry secondEntry = stream.getNextEntry();
            assertEquals(secondEntryName, secondEntry.getName());
            len = stream.read(buffer);
            assertEquals("ࠀ🦗", new String(buffer, 0, len, encodedFile.charset()));
            stream.closeEntry();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testZipFileWithShiftJIS() throws IOException {
        EncodedFile encodedFile = new EncodedFile(file, shiftJis);
        assertEquals(file, encodedFile.file());
        assertEquals(shiftJis, encodedFile.charset());
        // Call the zipFile() method
        try (var zipFile = encodedFile.openZipFile()) {
            assertNotNull(zipFile);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testZipFileWithUtf8() throws IOException {
        EncodedFile encodedFile = new EncodedFile(file, utf8);
        assertEquals(file, encodedFile.file());
        assertEquals(utf8, encodedFile.charset());
        assertThrows(IOException.class, () -> encodedFile.openZipFile());
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