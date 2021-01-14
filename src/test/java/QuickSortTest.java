import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;


public class QuickSortTest extends Assert {

    private final int MAX_ARR_SIZE = 1000000;
    private final int AVAILABLE_THREAD = 4;

    @Test
    public void testQuickSort() {
        List<Integer> list = new ArrayList<>();
        List<Integer> listResult = new ArrayList<>();

        for (int i = 0; i < MAX_ARR_SIZE; i++) {
            list.add(MAX_ARR_SIZE - i - 1);
            listResult.add(i);
        }

        Collections.shuffle(list);
        long start = System.nanoTime();
        QuickSort.quickSort(list, 0, list.size() - 1);
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
        Collections.shuffle(list);
        runParallelTest(list);
        assertEquals(list, listResult);

    }

    public void runParallelTest(List<Integer> list) throws ExecutionException, InterruptedException {
        CopyOnWriteArrayList<Future> threadList = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newWorkStealingPool();

        long start = System.nanoTime();
        ParallelQuickSort parallelQuickSort =
                new ParallelQuickSort(list, 0, list.size() - 1,
                        executor, threadList, AVAILABLE_THREAD);
        threadList.add(executor.submit(parallelQuickSort));
        while (!threadList.isEmpty()) {
   //блокируем потоки и ждем завершения задачи
            threadList.remove(0).get();
        }
        executor.shutdown();
        long end = System.nanoTime();
        System.out.println("\ntestParallelQuickSort " + (end - start));
    }
}