package sort;

import java.util.List;

public class QuickSort {

    public static void quickSort(List<Integer> list, int left, int right) {
        int pivot, low, high;
        int temp;
        if (left >= right)
            return;
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

        if (pivot > left)
            quickSort(list, left, pivot - 1);

        if (right > pivot)
            quickSort(list, pivot + 1, right);
    }
}