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

    private static int countThread = 0;

    public ParallelQuickSort(List<Integer> list, int left, int right,
                             ExecutorService executor, CopyOnWriteArrayList<Future> threadList, int availableThreads) {
        this.list = list;
        this.left = left;
        this.right = right;
        this.executor = executor;
        this.threadList = threadList;
        this.availableThreads = availableThreads;
    }

    private void quickSort(List<Integer> list, int left, int right) {
        if (left < right) {
            int pivot = partition(list, left, right);
            quickSort(list, left, pivot - 1);
            quickSort(list, pivot + 1, right);
        }
    }

    @Override
    public void run() {
        parallelQuickSort(list, left, right);
    }

    private int partition(List<Integer> list, int left, int right) {
        int pivot, low, high;
        int temp;
        if (left >= right)
            return -1;
        pivot = list.get(left);
        low = left;
        high = right;

        while (low < high) {
            while (list.get(low) <= pivot && low < right)
                low++;
            while (list.get(high) > pivot)
                high--;
            if (low < high) {
                temp = list.get(low);
                list.set(low, list.get(high));
                list.set(high, temp);
            }
        }
        list.set(left, list.get(high));
        list.set(high, pivot);

        return pivot;
    }

    private void parallelQuickSort(List<Integer> list, int left, int right) {

        int pivot = partition(list, left, right);

        if (pivot > left) {
            if (countThread < availableThreads) {
                countThread++;
                threadList.add(executor.submit(
                        new ParallelQuickSort(list, left, pivot, executor, threadList,availableThreads)));
            } else {
                quickSort(list, left, pivot);
            }
        }

        if (right >= pivot) {
            if (countThread < availableThreads) {
                countThread++;
                threadList.add(executor.submit(
                        new ParallelQuickSort(list, pivot + 1, right, executor, threadList,availableThreads)));
            } else {
                quickSort(list, pivot + 1, right);
            }
        }
    }
}