package ass1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * The benefit of using ForkJoin is that it is better at handling larger datasets (as it is parallel), it makes
 * use of work-stealing (like CompletableFutures), and it is perfect for naturally recursive tasks. Being good at
 * naturally recursive tasks is convenient for the merge-sort algorithm as it is
 * generally implemented using a recursive algorithm. We also maintain control over the detail of how operations
 * are executed while having some structure.
 *
 * Each recursive task goes into the pool, and then a pool of threads executes tasks. The advantage is that threads
 * are executing tasks most of the time as opposed to being used just for waiting.
 *
 * From using ForkJoin I've learned that each RecursiveTask doesn't actually use an entire thread when executing, something
 * that I didn't observe in the other implementations of the merge sort algorithm in this assignment.
 *
 * @author 300492211
 */
public class MParallelSorter3 implements Sorter {

//    Thanks to the following sources for helping me in this part of the assignment:
//    - Baeldung: https://www.baeldung.com/java-fork-join
//    - Hackernoon: https://hackernoon.com/parallel-merge-sort-with-forkjoin-framework


    static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();

    @Override
    public <T extends Comparable<? super T>> List<T> sort(List<T> list) {
        return FORK_JOIN_POOL.invoke(new ForkJoinSorter<T>(list));
    }

    class ForkJoinSorter<T extends Comparable<? super T>> extends RecursiveTask<List<T>> {
        // Threshold to determine when to delegate to a sequential sorter
        private static final int THRESHOLD = 20;
        private List<T> toSort;

        public ForkJoinSorter(List<T> list) {
            this.toSort = list;
        }


        /**
         * Method taken from the ISequentialSorter class for use in cases
         * with elements less than the threshold.
         *
         * @param list
         * @param <T>
         * @return
         */
        public <T extends Comparable<? super T>> List<T> sequentialSort(List<T> list) {
            List<T> result = new ArrayList<>();
            for (T l : list) {
                insert(result, l);
            }
            return result;
        }

        /**
         * Method taken from the ISequentialSorter class.
         *
         * @param list
         * @param elem
         * @param <T>
         */
        <T extends Comparable<? super T>> void insert(List<T> list, T elem) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).compareTo(elem) < 0) {
                    continue;
                }
                list.add(i, elem);
                return;
            }
            list.add(elem);
        }

        /**
         * Sorts a list by splitting it in half, and merging
         * the halves while recursively calling sort()
         * on each half
         *
         * @return
         */
        @Override
        protected List<T> compute() {
            if (this.toSort.size() < THRESHOLD) {
                return sequentialSort(this.toSort);
            }

            // Find the mid point of the list to allow for easier splitting
            int midPoint = this.toSort.size() / 2;

//            Fork each half of the merge-sort
//            Recursively sorts the list by using the compute method of RecursiveTask
            ForkJoinSorter<T> left = new ForkJoinSorter<>(this.toSort.subList(0, midPoint));
            ForkJoinSorter<T> right = new ForkJoinSorter<>(this.toSort.subList(midPoint, this.toSort.size()));
            invokeAll(left, right);

//            Return the two halves merged together as one list
            return merge(left.join(), right.join());
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


}