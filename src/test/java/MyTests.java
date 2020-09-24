import com.test.Main;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

public class MyTests extends Assert {

    @Test
    public void setFirstMap() {
        List<List<String>> list_marks = new ArrayList<>();

        list_marks.add(new ArrayList<>(Arrays.asList("mark23", "4")));
        list_marks.add(new ArrayList<>(Arrays.asList("mark23", "5")));
        list_marks.add(new ArrayList<>(Arrays.asList("mark23", "8")));
        list_marks.add(new ArrayList<>(Arrays.asList("mark35", "12")));
        list_marks.add(new ArrayList<>(Arrays.asList("markF2", "4")));
        list_marks.add(new ArrayList<>(Arrays.asList("mark35", "4")));
        list_marks.add(new ArrayList<>(Arrays.asList("markFT", "3")));

        Map<String, Long> result = Main.setFirstMap(list_marks, new HashMap<>());

        assertEquals(17L, (long) result.get("mark23"));
        assertEquals(16L, (long) result.get("mark35"));
        assertEquals(4L, (long) result.get("markF2"));
        assertEquals(3L, (long) result.get("markFT"));
    }

    @Test
    public void changeEmptyToNull() {
        Map<String, Optional<Long>> map = new HashMap<>();
        map.put("mark23", Optional.empty());
        map.put("mark35", Optional.of(0L));
        map.put("markFX", Optional.empty());
        map.put("markF2", Optional.empty());
        map.put("markFT", Optional.of(0L));

        Map<String, Object> result = Main.changeEmptyToNull(map);

        assertNull(result.get("mark23"));
        assertNotNull(result.get("mark35"));
        assertNull(result.get("markFX"));
        assertNull(result.get("markF2"));
        assertNotNull(result.get("markFT"));
    }

    @Test
    public void sortValuesMap() {
        Map<String, List<Integer>> list_marks = new HashMap<>();

        list_marks.put("mark23", new ArrayList<>(Arrays.asList(1, 4, 2, 6, 2, 4, 5, 6)));
        list_marks.put("mark35", new ArrayList<>(Arrays.asList(6, 4, 5, 6)));
        list_marks.put("markF2", new ArrayList<>(Arrays.asList(7, 4, 2, 6, 1, 3)));
        list_marks.put("markFT", new ArrayList<>(Arrays.asList(8, 4, 6, 6, 7, 4, 7)));

        Map<String, List<Integer>> result = Main.sortValuesMap(list_marks);

        assertEquals(new ArrayList<>(Arrays.asList(6, 6, 5, 4, 4, 2, 2, 1)), result.get("mark23"));
        assertEquals(new ArrayList<>(Arrays.asList(6, 6, 5, 4)), result.get("mark35"));
        assertEquals(new ArrayList<>(Arrays.asList(7, 6, 4, 3, 2, 1)), result.get("markF2"));
        assertEquals(new ArrayList<>(Arrays.asList(8, 7, 7, 6, 6, 4, 4)), result.get("markFT"));

    }
}
