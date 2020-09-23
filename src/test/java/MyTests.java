import com.test.Main;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class MyTests extends Assert {

    @Before
    public void init() throws Exception {
         Main.main(null);
    }

    @Test
    public void countElements() {
        assertEquals(5, Main.getRecordsForJsonOne().size());
        assertEquals(7, Main.getRecordsForJsonTwo().size());
        assertEquals(5, Main.getRecordsForJsonThree().size());

        assertEquals(72, Main.getRecordsForJsonOne().get("mark17").intValue());
    }

    @Test
    public void fileExists() {
        assertTrue(Files.exists(Paths.get(Main.OUTPUT_FILE_1)));
        assertTrue(Files.exists(Paths.get(Main.OUTPUT_FILE_2)));
        assertTrue(Files.exists(Paths.get(Main.OUTPUT_FILE_3)));
    }


    @Test
    public void isNull() {
        assertNull(Main.getRecordsForJsonTwo().get("markFX"));
        assertNull(Main.getRecordsForJsonTwo().get("mark23"));
    }

    @Test(expected = FileNotFoundException.class)
    public void fileNotFound() throws Exception {
        Main.start("qwerty");
    }

}