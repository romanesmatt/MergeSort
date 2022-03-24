package ass1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The benefit of using the sequential algorithm is that we don't have to use parallelism and threads, therefore
 * allowing for a smaller implementation (i.e smaller codebase than with parallelism and threads). We also avoid the issues
 * that are often observed when using parallelism and threads i.e performance issues if implemented incorrectly.
 * <p>
 * Using threads can be a costly operation, and there are many issues that can arise when we need to communicate between
 * processes. As this is the case, the sequential algorithm is actually faster when dealing with cases that have
 * a small number of elements. Thus when the number of elements in a case is below a certain threshold
 * (i.e 20 for this assignment) we might as well delegate to a sequential algorithm as there is little
 * benefit to use a parallel algorithm in these cases.
 * <p>
 * From implementing a sequential merge-sort algorithm  I've learnt that a sequential algorithm is
 * actually faster when dealing with cases with a smaller number of elements.
 *
 * @author 300492211
 */
public class MSequentialSorter implements Sorter {
    //Thanks to Stack Overflow for helping me in this part of the assignment
//    https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjIs6u1zN32AhXEeN4KHV52BckQFnoECBIQAw&url=https%3A%2F%2Fwww.geeksforgeeks.org%2Fconcurrency-in-operating-system%2F&usg=AOvVaw1A8YWf4ptqSojr9TcnRinT
//    https://stackoverflow.com/questions/30875497/merge-sort-list-java

    @Override
    public <T extends Comparable<? super T>> List<T> sort(List<T> list) {

        List<T> temp = new ArrayList<T>(list);

        if (temp.size() < 2) {
            return temp;
        }
// Find the mid point of the list to allow for easier splitting
        int midPoint = temp.size() / 2;

        return merge(sort(temp.subList(0, midPoint)),
                sort(temp.subList(midPoint, temp.size())));

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