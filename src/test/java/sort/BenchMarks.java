package sort;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Fork(value = 1)
@Warmup(iterations = 30, time = 1)
@Measurement(iterations = 30, time = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
public class BenchMarks {

    public static void main(final String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void oneThread() throws ExecutionException, InterruptedException {
        parallelQuickSort(1);
    }

    @Benchmark
    public void twoThread() throws ExecutionException, InterruptedException {
        parallelQuickSort(2);
    }

    @Benchmark
    public void threeThread() throws ExecutionException, InterruptedException {
        parallelQuickSort(3);
    }

    @Benchmark
    public void fourThread() throws ExecutionException, InterruptedException {
        parallelQuickSort(4);
    }

    @Benchmark
    public void eightThread() throws ExecutionException, InterruptedException {
        parallelQuickSort(8);
    }

    @Benchmark
    public void twentyThread() throws ExecutionException, InterruptedException {
        parallelQuickSort(20);
    }

    private void parallelQuickSort(int threadsNum) throws ExecutionException, InterruptedException {
        List<Integer> list = new ArrayList<>();
        int ARRAY_SIZE = 10000000;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            list.add(ARRAY_SIZE - i - 1);
        }
        CopyOnWriteArrayList<Future> threadList = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newWorkStealingPool();
        int cores = Runtime.getRuntime().availableProcessors();
        ParallelQuickSort parallelQuickSort =
                new ParallelQuickSort(list, 0, list.size() - 1,
                        executor, threadList, threadsNum);
        threadList.add(executor.submit(parallelQuickSort));
        while (!threadList.isEmpty()) {
            //блокируем потоки и ждем завершения задачи
            threadList.remove(0).get();
        }
        executor.shutdown();
    }
}
