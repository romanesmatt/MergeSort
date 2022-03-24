# MergeSort

This is the first project for NWEN 303 (Concurrent Programming) at Victoria University of Wellington. The objective of
the project was to gain an understanding of parallel programming with the use of Futures, CompletableFutures and the
ForkJoin framework.

* <a href="sequential-algorithm">Sequential Algorithm</a>
* <a href="merge-sort-with-future">Merge Sort with Future</a>
* <a href="merge-sort-with-completablefuture">Merge Sort with CompletableFuture</a>
* <a href="merge-sort-with-the-fork-join-framework">Merge Sort with the ForkJoin framework</a>
* <a href="testing-the-algorithms">Testing the Algorithms</a>

## Implementations

This project includes multiple implementations of the Merge Sort algorithm, starting with a sequential implementation
and concluding with an implementation that uses the ForkJoin framework.

### Sequential Algorithm

The benefit of using the sequential algorithm is that we don't have to use parallelism and threads, therefore allowing
for a smaller implementation (i.e smaller codebase than with parallelism and threads). We also avoid the issues that are
often observed when using parallelism and threads i.e performance issues if implemented incorrectly.
<p>
 Using threads can be a costly operation, and there are many issues that can arise when we need to communicate between
 processes. As this is the case, the sequential algorithm is actually faster when dealing with cases that have
 a small number of elements. Thus when the number of elements in a case is below a certain threshold
 (i.e 20 for this assignment) we might as well delegate to a sequential algorithm as there is little
 benefit to use a parallel algorithm in these cases.
 <p>
 From implementing a sequential merge-sort algorithm I've learnt that a sequential algorithm is
 actually faster when dealing with cases with a smaller number of elements.

### Merge Sort with Future

The benefit of using Futures is that the algorithm is faster in cases with a large number of elements (as it is
parallel) and that we have greater control of how we want to deal with execution and exceptions. In addition, we have
control over the detail of execution because we have to specify when we want the algorithm to fork when using Futures.
<p> My original thread pool was a FixedThreadPool, however I observed thread exhaustion even when capped at 1000 threads.
 Replacing it with CachedThreadPool did solve the problem, but it also slowed down the algorithm due to the number of threads
 being created (and not capped like in FixedThreadPool). So I eventually settled with WorkStealingPool
 which sped up the performance of the algorithm due to reduced contention between the threads due to workers being
 able to take tasks from other workers' queues if a worker no longer has tasks in its own queue.
 <p>
 What I learnt from using Futures is that having a custom get() method for Futures is preferable because it meant that I
 could separate the exception handling from the algorithm itself (i.e didn't have to throw exceptions INSIDE the algorithm
 or add exceptions to the sort() method signature.)

### Merge Sort with CompletableFuture

The benefit of using CompletableFutures is that it is better at handling larger datasets (as it is parallel)
and it makes use of work-stealing. Work-stealing allows the algorithm to execute quicker because workers are able to
take tasks from other workers' queues if a worker no longer has tasks in its own queue. This means that merge() does not
block when we use the thenCombineAsync() method to pass the left and right halves of the list into merge().
<p>
 Because CompletableFutures use the work-stealing algorithm by default, I don't have to create a new thread pool
 like I did when using Futures in MParallelSorter1. One thing I found particularly interesting is that with
 workStealingPool(), the Futures I used in MParallelSorter1 were on average quicker than the CompletableFutures
 used here despite using the ForkJoinPool.commonPool() method (which in itself use the work-stealing algorithm).
 <p>
 What I learned from using CompletableFutures is that while it is more convenient to use these as opposed to a Future,
 in certain circumstances a Future may still outperform a CompletableFuture
 (especially when the Future is using workStealingPool()).

### Merge Sort with the ForkJoin Framework

The benefit of using ForkJoin is that it is better at handling larger datasets (as it is parallel), it makes use of
work-stealing (like CompletableFutures), and it is perfect for naturally recursive tasks. Being good at naturally
recursive tasks is convenient for the merge-sort algorithm as it is generally implemented using a recursive algorithm.
We also maintain control over the detail of how operations are executed while having some structure.
<p>
 Each recursive task goes into the pool, and then a pool of threads executes tasks. The advantage is that threads
 are executing tasks most of the time as opposed to being used just for waiting.
 <p>
 From using ForkJoin I've learned that each RecursiveTask doesn't actually use an entire thread when executing, something
 that I didn't observe in the other implementations of the merge sort algorithm in this assignment.

## Testing the Algorithms

The TestPerformance class contains test cases that compares each implementation of the merge sort algorithm against
different data types; Float, Integer, String and a custom Point object. The class also includes a 'naive' method that
compares the performance of each implementation and prints the results.

**NOTE**: The default warmUp value of 20,000 produces more accurate results, however it can be tiresome to wait for.
Reduce the number of warmups to 200 while keeping the number of runs the same to produce somewhat conclusive results.
