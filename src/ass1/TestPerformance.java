package ass1;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Thanks to the following sources for helping me in this part of the assignment:
 * https://stackoverflow.com/questions/15126887/warm-up-the-code-before-start-measuring-the-performance
 * https://stackoverflow.com/questions/504103/how-do-i-write-a-correct-micro-benchmark-in-java
 */
public class TestPerformance {
    /**
     * Measures how long in milliseconds that a Runnable takes to run. The method has a
     * 'warm up' stage in so that algorithm can run a few thousand times before we start
     * measuring how long it takes for real. This is necessary to get the JIT correctly
     * optimise the algorithm (so all initialisations and compilations have been triggered).
     *
     * @param r
     * @param warmUp
     * @param runs
     * @return
     */
    long timeOf(Runnable r, int warmUp, int runs) {
        System.gc();
        for (int i = 0; i < warmUp; i++) {
            r.run();
        }
        long time0 = System.currentTimeMillis();
        for (int i = 0; i < runs; i++) {
            r.run();
        }
        long time1 = System.currentTimeMillis();
        return time1 - time0;
    }

    /**
     * Takes a sorter implementation and sorts a dataset. The method measures how long
     * it takes for the sorter to sort the list using the timeOf() method. The method
     * then prints out the name of the implementation, and the time in seconds it took
     * to run.
     *
     * @param s
     * @param name
     * @param dataset
     * @param <T>
     */
    <T extends Comparable<? super T>> void msg(Sorter s, String name, T[][] dataset) {
        long time = timeOf(() -> {
            for (T[] l : dataset) {
                s.sort(Arrays.asList(l));
            }
//            NOTE: During testing, I reduced the number of warm ups to 20000 and runs to 20 to
//            to shorten the tests' execution time.
        }, 200, 20);//realistically 20.000 to make the JIT do his job..
        System.out.println(name + " sort takes " + time / 1000d + " seconds");
    }

    /**
     * Takes a dataset and calls the msg() method using each implementation of Sorter.
     * The method produces an output for how long each implementation of Sorter takes
     * to run (using MSequentialSorter(), MParallelSorter1(), MParallelSorter2(),
     * and MParallelSorter3()).
     *
     * @param dataset A dataset to be sorted
     * @param <T>
     */
    <T extends Comparable<? super T>> void msgAll(T[][] dataset) {
//        msg(new ISequentialSorter(), "Sequential insertion", TestBigInteger.dataset);//so slow
        //uncomment the former line to include performance of ISequentialSorter
        msg(new MSequentialSorter(), "Sequential merge sort", dataset);
        msg(new MParallelSorter1(), "Parallel merge sort (futures)", dataset);
        msg(new MParallelSorter2(), "Parallel merge sort (completablefutures)", dataset);
        msg(new MParallelSorter3(), "Parallel merge sort (forkJoin)", dataset);
    }

    @Test
    void testBigInteger() {
        System.out.println("On the data type BigInteger");
        msgAll(TestBigInteger.dataset);
    }

    @Test
    void testFloat() {
        System.out.println("On the data type Float");
        msgAll(TestFloat.dataset);
    }

    @Test
    void testPoint() {
        System.out.println("On the data type Point");
        msgAll(TestPoint.dataset);
    }

    @Test
    void testString() {
        System.out.println("On the data type String");
        msgAll(TestString.dataset);
    }
}
/*
With the model solutions, on a lab machine (2019) we may get those results:
On the data type Float
Sequential merge sort sort takes 1.178 seconds
Parallel merge sort (futures) sort takes 0.609 seconds
Parallel merge sort (completablefutures) sort takes 0.403 seconds
Parallel merge sort (forkJoin) sort takes 0.363 seconds
On the data type Point
Sequential merge sort sort takes 1.373 seconds
Parallel merge sort (futures) sort takes 0.754 seconds
Parallel merge sort (completablefutures) sort takes 0.541 seconds
Parallel merge sort (forkJoin) sort takes 0.48 seconds
On the data type BigInteger
Sequential merge sort sort takes 1.339 seconds
Parallel merge sort (futures) sort takes 0.702 seconds
Parallel merge sort (completablefutures) sort takes 0.452 seconds
Parallel merge sort (forkJoin) sort takes 0.492 seconds

On another lab machine in 2021 we get those results:
//they must have optimized sequential execution quite a bit!
On the data type Float
Sequential merge sort sort takes 0.635 seconds
Parallel merge sort (futures) sort takes 0.475 seconds //with a smart trick we may get 0.241 instead
Parallel merge sort (completablefutures) sort takes 0.25 seconds
Parallel merge sort (forkJoin) sort takes 0.253 seconds
On the data type Point
Sequential merge sort sort takes 0.76 seconds //with a smart trick we may get 0.296 instead
Parallel merge sort (futures) sort takes 0.505 seconds
Parallel merge sort (completablefutures) sort takes 0.279 seconds
Parallel merge sort (forkJoin) sort takes 0.296 seconds
On the data type BigInteger
Sequential merge sort sort takes 0.871 seconds
Parallel merge sort (futures) sort takes 0.574 seconds //with a smart trick we may get 0.372 instead
Parallel merge sort (completablefutures) sort takes 0.354 seconds
Parallel merge sort (forkJoin) sort takes 0.338 seconds
 
*/