package sort;

import java.util.List;

public class QuickSort {

    public static void quickSort(List<Integer> list, int left, int right) {

        if (left >= right)
            return;

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

        if (left < high)
            quickSort(list, left, high);

        if (right > low)
            quickSort(list, low, right);
    }
}