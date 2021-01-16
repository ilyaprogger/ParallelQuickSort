package sort;

import java.util.List;

public class QuickSort {

    public static void quickSort(List<Integer> list, int left, int right) {
        if (left >= right)
            return;
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

        if (pivot > left)
            quickSort(list, left, pivot - 1);

        if (right > pivot)
            quickSort(list, pivot + 1, right);
    }
}