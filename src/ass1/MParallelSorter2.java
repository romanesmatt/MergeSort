package ass1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * The benefit of using CompletableFutures is that it is better at handling larger datasets (as it is parallel)
 * and it makes use of work-stealing. Work-stealing allows the algorithm to execute quicker because workers are
 * able to take tasks from other workers' queues if a worker no longer has tasks in its own queue. This means that
 * merge() does not block when we use the thenCombineAsync() method to pass the left and right halves of the list into
 * merge().
 * <p>
 * Because CompletableFutures use the work-stealing algorithm by default, I don't have to create a new thread pool
 * like I did when using Futures in MParallelSorter1. One thing I found particularly interesting is that with
 * workStealingPool(), the Futures I used in MParallelSorter1 were on average quicker than the CompletableFutures
 * used here despite using the ForkJoinPool.commonPool() method (which in itself use the work-stealing algorithm).
 *
 * What I learned from using CompletableFutures is that while it is more convenient to use these as opposed to a Future,
 * in certain circumstances a Future may still outperform a CompletableFuture
 * (especially when the Future is using workStealingPool()).
 *
 * <p>
 * Thanks to the following sources for helping me in this part of the assignment:
 * https://www.callicoder.com/java-8-completablefuture-tutorial/
 * https://reflectoring.io/java-completablefuture/
 * https://dzone.com/articles/be-aware-of-forkjoinpoolcommonpool
 *
 * @author 300492211
 */
public class MParallelSorter2 implements Sorter {
    // Threshold to determine when to delegate to a sequential sorter
    private static int THRESHOLD = 20;

    /**
     * Sorts a list by splitting it in half, and merging the halves while recursively calling sort()
     * on each half.
     * Uses CompletableFutures to fork the merge-sort by making the 'left' half of the list a Future.
     *
     * @param list
     * @param <T>
     * @return
     */
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
//        Now using CompletableFuture

        CompletableFuture<List<T>> left = CompletableFuture.supplyAsync(() -> sort(temp.subList(0, midPoint)));
        CompletableFuture<List<T>> right = CompletableFuture.supplyAsync(() -> sort(temp.subList(midPoint, temp.size())));
        CompletableFuture<List<T>> merged = left.thenCombineAsync(right, (leftHalf, rightHalf) -> { //Previous implementation used thenCombine() which was slower than MParallelSorter1 with Futures
            return merge(leftHalf, rightHalf);
        });

        return merged.join();
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