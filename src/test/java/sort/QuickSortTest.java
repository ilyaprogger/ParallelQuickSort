package sort;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static sort.QuickSort.quickSort;

public class QuickSortTest extends Assert {

    private final int MAX_ARR_SIZE = 2000000;

    @Test
    public void testQuickSort() {
        List<Integer> list = new ArrayList<>();
        List<Integer> listResult = new ArrayList<>();

        for (int i = 0; i < MAX_ARR_SIZE; i++) {
            list.add(MAX_ARR_SIZE - i - 1);
            listResult.add(i);
        }

        long start = System.nanoTime();
        quickSort(list, 0, list.size() - 1);
        long end = System.nanoTime();
        System.out.println("\n Just QuickSort " + (end - start));
        assertEquals(list, listResult);
    }

    @Test
    public void testParallelQuickSort() throws ExecutionException, InterruptedException {
        List<Integer> list = new ArrayList<>();
        List<Integer> listResult = new ArrayList<>();

        for (int i = 0; i < MAX_ARR_SIZE; i++) {
            list.add(MAX_ARR_SIZE - i - 1);
            listResult.add(i);
        }

        long start = System.nanoTime();
        testWorkStealingPool(list);
        long end = System.nanoTime();
        System.out.println("\n Just QuickSort " + (end - start));
        assertEquals(list, listResult);
    }

    @Test
    public void testFixedAndWOrkStealing() throws ExecutionException, InterruptedException {
        List<Integer> list = new ArrayList<>();
        List<Integer> listResult = new ArrayList<>();

        for (int i = 0; i < MAX_ARR_SIZE; i++) {
            list.add(MAX_ARR_SIZE - i - 1);
            listResult.add(MAX_ARR_SIZE - i - 1);
        }
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            testFixedThreadPool(list);
            long end = System.nanoTime();
            System.out.println("\ntestFixedThreadPool " + (end - start));
            long start2 = System.nanoTime();
            testWorkStealingPool(listResult);
            long end2 = System.nanoTime();
            System.out.println("\ntestWorkStealingPool " + (end2 - start2));
        }
        assertEquals(list, listResult);

    }

    public void testFixedThreadPool(List<Integer> list) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        runParallelTest(list, executor);
    }

    public void testWorkStealingPool(List<Integer> list) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newWorkStealingPool();
        runParallelTest(list, executor);
    }

    public void runParallelTest(List<Integer> list, ExecutorService executor)
            throws ExecutionException, InterruptedException {
        CopyOnWriteArrayList<Future> threadList = new CopyOnWriteArrayList<>();

        int cores = Runtime.getRuntime().availableProcessors();
        ParallelQuickSort parallelQuickSort =
                new ParallelQuickSort(list, 0, list.size() - 1, executor,
                        threadList, cores);
        threadList.add(executor.submit(parallelQuickSort));
        while (!threadList.isEmpty()) {
//блокируем потоки и ждем завершения задачи
            threadList.remove(0).get();
        }
        executor.shutdown();
    }
}
