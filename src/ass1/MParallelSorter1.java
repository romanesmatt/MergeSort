package ass1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The benefit of using Futures is that the algorithm is faster in cases with a large number of elements (as
 * it is parallel) and that we have greater control of how we want to deal with execution and exceptions. In addition, we have
 * control over the detail of execution because we have to specify when we want the algorithm to fork when using Futures.
 * <p>
 * My original thread pool was a FixedThreadPool, however I observed thread exhaustion even when capped at 1000 threads.
 * Replacing it with CachedThreadPool did solve the problem, but it also slowed down the algorithm due to the number of threads
 * being created (and not capped like in FixedThreadPool). So I eventually settled with WorkStealingPool
 * which sped up the performance of the algorithm due to reduced contention between the threads due to workers being
 * able to take tasks from other workers' queues if a worker no longer has tasks in its own queue.
 * <p>
 * What I learnt from using Futures is that having a custom get() method for Futures is preferable because it meant that I
 * could separate the exception handling from the algorithm itself (i.e didn't have to throw exceptions INSIDE the algorithm
 * or add exceptions to the sort() method signature.)
 *
 * <p>
 * Thanks to the following sources for helping me in this part of the assignment:
 * https://zetcode.com/java/future/
 * https://stackoverflow.com/questions/41337451/detailed-difference-between-java8-forkjoinpool-and-executors-newworkstealingpool
 * https://medium.com/quick-code/java-concurrency-in-a-nutshell-types-of-thread-pools-part-3-ef95510b7ce8
 *
 * @author 300492211
 */
public class MParallelSorter1 implements Sorter {
    //    Thread pool for using Futures
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newWorkStealingPool();
    // Threshold to determine when to delegate to a sequential sorter
    private static final int THRESHOLD = 20;

    @Override
    public <T extends Comparable<? super T>> List<T> sort(List<T> list) {
        List<T> temp = new ArrayList<T>(list);

        if (temp.size() < 2) {
            return temp;
        }

//        Delegating to MSequentialSorter for cases with elements less than the threshold
        if (temp.size() < THRESHOLD) {
            MSequentialSorter sequentialSorter = new MSequentialSorter();
            return sequentialSorter.sort(temp);
        }

        // Find the mid point of the list to allow for easier splitting
        int midPoint = temp.size() / 2;

//        Fork each half of the merge sort.
//        Recursively calls sort() on the left and right halves of the split list
//        Now using Future
        Future<List<T>> left = EXECUTOR_SERVICE.submit(() -> sort(temp.subList(0, midPoint)));
        List<T> right = sort(temp.subList(midPoint, temp.size()));

//        Return the halves merged together as one list
        return merge(get(left), right);
    }

    /**
     * A custom get method for Futures. Takes exception
     * handling away from the sort() method and does
     * it here instead.
     * Inspired by method from lecture 4.
     *
     * @param listFuture
     * @param <T>
     * @return
     */
    public static <T extends Comparable<? super T>> List<T> get(Future<List<T>> listFuture) {
        try {
            return listFuture.get();
        } catch (
                InterruptedException e) { //Unlikely to occur, although as a precaution...
            Thread.currentThread().interrupt();
            throw new Error(e);
        } catch (ExecutionException e) {
            Throwable t = e.getCause(); //Propagate unchecked exceptions
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            }
            throw new Error("Unexpected Checked Exception", t);
        }
    }

    /**
     * Implementation of the merge sort algorithm.
     * Takes two halves of a list and merges them
     * into a new list while maintaining ordering.
     * <p>
     * Thanks to Baeldung for the tutorial on Merge Sort:
     * https://www.baeldung.com/java-merge-sort
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    public <T extends Comparable<? super T>> List<T> merge(List<T> left, List<T> right) {
//    Temporary list
        ArrayList<T> temp = new ArrayList<T>();
//Keeping track of each list
        int i = 0, j = 0;

//        Loops until both halves have been sorted and merged into a list
        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) < 0) {
                temp.add(left.get(i));
                i++;
            } else {
                temp.add(right.get(j));
                j++;
            }
        }

//        If there are any remaining elements in each half
        temp.addAll(left.subList(i, left.size()));
        temp.addAll(right.subList(j, right.size()));

        return temp;
    }

}