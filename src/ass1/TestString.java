package ass1;

import org.junit.jupiter.api.Test;

import java.util.Random;

public class TestString {

    public static final String[][] dataset = {
//            Formula 1 teams in order of Constructors' Championship standings after the 2022 Bahrain Grand Prix
            {"ferrari", "mercedes", "haas", "alfaromeo", "alpine", "alphatauri", "astonmartin", "williams", "mclaren", "redbull"},
            {"ferrari", "mercedes", "haas", "alfaromeo", "alpine", "alphatauri", "astonmartin", "williams", "mclaren", "redbull"},
            {"ferrari", "mercedes", "haas", "alfaromeo", "alpine", "alphatauri", "astonmartin", "williams", "mclaren", "redbull"},
            {},
            manyOrdered(10000),
            manyReverse(10000),
            manyRandom(10000)

    };

//    NOTE: Test methods in this class were inspired by the other test classes provided

    private static String[] manyRandom(int size) {
        Random random = new Random(0);
        String[] result = new String[size];

        for (int i = 0; i < size; i++) {
            int num = random.nextInt();
            result[i] = new String(String.valueOf(num));
        }
        return result;
    }

    private static String[] manyReverse(int size) {
        String[] result = new String[size];

        for (int i = 0; i < size; i++) {
            result[i] = new String("99999" + (size - i));
        }
        return result;

    }

    private static String[] manyOrdered(int size) {
        String[] result = new String[size];

        for (int i = 0; i < size; i++) {
            result[i] = new String("99999" + (i));
        }
        return result;

    }

    @Test
    public void testISequentialSorter() {
        Sorter s = new ISequentialSorter();
        for (String[] l : dataset) {
            TestHelper.testData(l, s);
        }
    }

    @Test
    public void testMSequentialSorter() {
        Sorter s = new MSequentialSorter();
        for (String[] l : dataset) {
            TestHelper.testData(l, s);
        }
    }

    @Test
    public void testMParallelSorter1() {
        Sorter s = new MParallelSorter1();
        for (String[] l : dataset) {
            TestHelper.testData(l, s);
        }
    }

    @Test
    public void testMParallelSorter2() {
        Sorter s = new MParallelSorter2();
        for (String[] l : dataset) {
            TestHelper.testData(l, s);
        }
    }

    @Test
    public void testMParallelSorter3() {
        Sorter s = new MParallelSorter3();
        for (String[] l : dataset) {
            TestHelper.testData(l, s);
        }
    }


}
