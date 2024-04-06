package java_practice01;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class contains unit tests for the EncodedFileList class.
 */
public class EncodedFileListTest {
    private EncodedFileList encodedFileList;
    private File file;
    private Charset shiftJis;
    private File fileShiftJis;
    private Charset utf8;
    private File fileUtf8;

    @BeforeEach
    void setUp() {
        List<EncodedFile> fileList = new ArrayList<>();
        encodedFileList = new EncodedFileList(fileList);
        file = new File("src\\test\\resources\\test.zip");
        shiftJis = Charset.forName("Shift_JIS");
        fileShiftJis = new File("src\\test\\resources\\testShiftJis.zip");
        utf8 = Charset.forName("UTF-8");
        fileUtf8 = new File("src\\test\\resources\\testUtf8.zip");
    }

    /**
     * Test case for the collect method in the EncodedFileList class.
     * Verifies that the collect method correctly adds encoded files to the list.
     */
    @Test
    void testCollect() {
        encodedFileList.collectFile(fileShiftJis, shiftJis, utf8);

        assertEquals(2, encodedFileList.size());
        assertEquals(fileShiftJis, encodedFileList.get(0).file());
        assertEquals(shiftJis, encodedFileList.get(0).charset());
        assertEquals(fileShiftJis, encodedFileList.get(1).file());
        assertEquals(utf8, encodedFileList.get(1).charset());
    }

    @Test
    void testCollectFiles() {
        encodedFileList.collectCharset(utf8, fileShiftJis, fileUtf8);

        assertEquals(2, encodedFileList.size());
        assertEquals(utf8, encodedFileList.get(0).charset());
        assertEquals(fileShiftJis, encodedFileList.get(0).file());
        assertEquals(utf8, encodedFileList.get(1).charset());
        assertEquals(fileUtf8, encodedFileList.get(1).file());
    }

    /**
     * Test case to verify that the collect method throws a NullPointerException
     * when a null file is passed as input.
     */
    @Test
    void testCollectNullFile() {
        List<Charset> charsets = Arrays.asList(shiftJis, utf8);
        assertThrows(NullPointerException.class, () -> encodedFileList.collect(null, charsets));
    }

    /**
     * Test case to verify that a NullPointerException is thrown when collecting
     * files with null charsets.
     */
    @Test
    void testCollectFileNullCharsets() {
        assertThrows(NullPointerException.class, () -> encodedFileList.collectFile(file, (Charset[]) null));
    }

    /**
     * Test case to verify that a NullPointerException is thrown when collecting
     * files with null charsets.
     */
    @Test
    void testCollectCharsetNullFiles() {
        assertThrows(NullPointerException.class, () -> encodedFileList.collectCharset(shiftJis, (File[]) null));
    }

    /**
     * Test case for the {@link EncodedFileList#findFirstValidZipFile()} method.
     * It verifies that the method correctly finds the first valid zip file in the
     * encoded file list.
     */
    @Test
    void testFindFirstValidZipFile() {
        encodedFileList.collect(Arrays.asList(fileShiftJis), Arrays.asList(utf8, shiftJis));
        Optional<EncodedFile> result = encodedFileList.findFirstValidZipFile();
        assertTrue(result.isPresent());
        result.ifPresent(encodedFile -> {
            assertEquals(fileShiftJis, encodedFile.file());
            assertEquals(shiftJis, encodedFile.charset());
            try (var zipFile = encodedFile.openZipFile()) {
                assertNotNull(zipFile);
            } catch (Exception e) {
                fail();
            }
        });
    }

    /**
     * Test case to verify the behavior of the {@code findFirstValidZipFile} method
     * when there are no valid zip files in the encoded file list.
     */
    @Test
    void testFindFirstValidZipFileNoValidFileByFile() {
        encodedFileList.collectFile(fileShiftJis, utf8);
        Optional<EncodedFile> result = encodedFileList.findFirstValidZipFile();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindFirstValidZipFileNoValidFileByCharset() {
        encodedFileList.collectCharset(utf8, fileShiftJis);
        Optional<EncodedFile> result = encodedFileList.findFirstValidZipFile();
        assertTrue(result.isEmpty());
    }

    @Test
    void testAvailables() {
        encodedFileList.collect(Arrays.asList(fileShiftJis, fileUtf8),
                Arrays.asList(utf8, shiftJis));
        assertEquals(4, encodedFileList.size());
        EncodedFileList result = encodedFileList.availables();
        assertEquals(2, result.size());
        //
        assertEquals(fileShiftJis, result.get(0).file());
        assertEquals(shiftJis, result.get(0).charset());
        //
        assertEquals(fileUtf8, result.get(1).file());
        assertEquals(utf8, result.get(1).charset());
    }

}