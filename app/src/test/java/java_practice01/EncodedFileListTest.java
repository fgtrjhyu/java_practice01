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
    private Charset eucJp;
    private Charset utf8;
    private Charset iso2022jp;

    @BeforeEach
    void setUp() {
        List<EncodedFile> fileList = new ArrayList<>();
        encodedFileList = new EncodedFileList(fileList);
        file = new File("src\\test\\resources\\test.zip");
        shiftJis = Charset.forName("Shift_JIS");
        eucJp = Charset.forName("euc-jp");
        utf8 = Charset.forName("UTF-8");
        iso2022jp = Charset.forName("ISO-2022-JP");
    }

    /**
     * Test case for the collect method in the EncodedFileList class.
     * Verifies that the collect method correctly adds encoded files to the list.
     */
    @Test
    void testCollect() {
        List<Charset> charsets = Arrays.asList(shiftJis, utf8, iso2022jp);
        encodedFileList.collect(file, charsets);

        assertEquals(3, encodedFileList.size());
        assertEquals(file, encodedFileList.get(0).file());
        assertEquals(shiftJis, encodedFileList.get(0).charset());
        assertEquals(file, encodedFileList.get(1).file());
        assertEquals(utf8, encodedFileList.get(1).charset());
        assertEquals(file, encodedFileList.get(2).file());
        assertEquals(iso2022jp, encodedFileList.get(2).charset());
    }

    /**
     * Test case to verify that the collect method throws a NullPointerException
     * when a null file is passed as input.
     */
    @Test
    void testCollectNullFile() {
        List<Charset> charsets = Arrays.asList(shiftJis, utf8, iso2022jp);
        assertThrows(NullPointerException.class, () -> encodedFileList.collect(null, charsets));
    }

    /**
     * Test case to verify that a NullPointerException is thrown when collecting
     * files with null charsets.
     */
    @Test
    void testCollectNullCharsets() {
        assertThrows(NullPointerException.class, () -> encodedFileList.collect(file, null));
    }

    /**
     * Test case for the {@link EncodedFileList#findFirstValidZipFile()} method.
     * It verifies that the method correctly finds the first valid zip file in the
     * encoded file list.
     */
    @Test
    void testFindFirstValidZipFile() {
        encodedFileList.collect(file, Arrays.asList(utf8, iso2022jp, shiftJis, eucJp));
        Optional<EncodedFile> result = encodedFileList.findFirstValidZipFile();
        assertTrue(result.isPresent());
        result.ifPresent(encodedFile -> {
            assertEquals(file, encodedFile.file());
            assertEquals(shiftJis, encodedFile.charset());
            try (var zipFile = encodedFile.zipFile()) {
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
    void testFindFirstValidZipFileNoValidFile() {
        encodedFileList.collect(file, Arrays.asList(utf8, iso2022jp));
        Optional<EncodedFile> result = encodedFileList.findFirstValidZipFile();
        assertTrue(result.isEmpty());
    }
}