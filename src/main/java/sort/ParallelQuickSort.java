package sort;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ParallelQuickSort extends Thread {
    private final List<Integer> list;
    private final int left;
    private final int right;
    private final ExecutorService executor;
    private final CopyOnWriteArrayList<Future> threadList;
    private final int availableThreads;

    private volatile int countThread;

    public ParallelQuickSort(List<Integer> list, int left, int right,
                             ExecutorService executor, CopyOnWriteArrayList<Future> threadList,
                             int availableThreads, int countThread) {
        this.list = list;
        this.left = left;
        this.right = right;
        this.executor = executor;
        this.threadList = threadList;
        this.availableThreads = availableThreads;
        this.countThread = countThread;
    }

    private void quickSort(List<Integer> list, int left, int right) {
        if (left < right) {
            int pivot = partition(list, left, right);
            quickSort(list, left, pivot);
            quickSort(list, pivot + 1, right);
        }
    }

    @Override
    public void run() {
        parallelQuickSort(list, left, right);
    }

    private int partition(List<Integer> list, int left, int right) {
        if (left >= right)
            return -1;
        int pivot = list.get(left), low = left, high = right;
        while (low < high) {
            while (low < high && list.get(high) >= pivot)
                high--;
            if (low < high) {
                list.set(low, list.get(high));
                low++;
                while (list.get(low) < pivot && low < high)
                    low++;
                if (low < high) {
                    list.set(high, list.get(low));
                    high--;
                }
            }
        }
        list.set(low, pivot);
        return low;
    }

    public synchronized boolean compare() {
        return countThread < availableThreads;
    }

    public synchronized void increment() {
        countThread++;
    }

    private static final Object object = new Object();

    private void parallelQuickSort(List<Integer> list, int left, int right) {

        int pivot = partition(list, left, right);
        synchronized (object) {
            if (pivot > left) {
                if (compare()) {
                    increment();
                    threadList.add(executor.submit(
                            new ParallelQuickSort(list, left, pivot, executor,
                                    threadList, availableThreads, countThread)));
                } else {
                    quickSort(list, left, pivot);
                }
            }
        }
        synchronized (object) {
            if (right >= pivot) {
                if (compare()) {
                    increment();
                    threadList.add(executor.submit(
                            new ParallelQuickSort(list, pivot + 1, right, executor,
                                    threadList, availableThreads, countThread)));
                } else {
                    quickSort(list, pivot + 1, right);
                }
            }
        }
    }
}