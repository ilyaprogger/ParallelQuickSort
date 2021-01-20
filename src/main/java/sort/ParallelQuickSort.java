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

    private static volatile int countThread = 0;

    public ParallelQuickSort(List<Integer> list, int left, int right,
                             ExecutorService executor, CopyOnWriteArrayList<Future> threadList,
                             int availableThreads) {
        this.list = list;
        this.left = left;
        this.right = right;
        this.executor = executor;
        this.threadList = threadList;
        this.availableThreads = availableThreads;
    }

    private void quickSort(List<Integer> list, int left, int right) {
        if (left < right) {
            int[] pivot = partition(list, left, right);
            quickSort(list, left, pivot[1]);
            quickSort(list, pivot[0], right);
        }
    }

    @Override
    public void run() {
        parallelQuickSort(list, left, right);
    }

    private int[] partition(List<Integer> list, int left, int right) {
        if (left >= right)
            return new int[]{-1};

        int pivot = list.get(left + (right - left) / 2);

        int low = left, high = right;
        while (low <= high) {
            while (list.get(low) < pivot) {
                low++;
            }

            while (list.get(high) > pivot) {
                high--;
            }

            if (low <= high) {
                int temp = list.get(low);
                list.set(low, list.get(high));
                list.set(high, temp);
                low++;
                high--;
            }
        }
        return new int[]{low, high};
    }

    private static final Object object = new Object();

    public boolean compare() {
        synchronized (object) {
            if (countThread < availableThreads) {
                countThread++;
                return true;
            } else {
                return false;
            }
        }
    }

    private void parallelQuickSort(List<Integer> list, int left, int right) {

        int[] pivot = partition(list, left, right);

        if (pivot[0] == -1) {
            return;
        }

        if (pivot[1] > left) {
            if (compare()) {
                threadList.add(executor.submit(
                        new ParallelQuickSort(list, left, pivot[1], executor,
                                threadList, availableThreads)));
            } else {
                quickSort(list, left, pivot[1]);
            }

        }
        if (right > pivot[0]) {
            if (compare()) {
                threadList.add(executor.submit(
                        new ParallelQuickSort(list, pivot[0], right, executor,
                                threadList, availableThreads)));
            } else {
                quickSort(list, pivot[0], right);
            }
        }
    }
}