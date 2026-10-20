package campus.ds;

import java.util.Arrays;

public class DSAlgorithms {

    public static int[] sort(int[] values, String method) {
        int[] a = Arrays.copyOf(values, values.length);

        switch (method) {
            case "Bubble Sort":
                bubbleSort(a);
                break;
            case "Selection Sort":
                selectionSort(a);
                break;
            case "Insertion Sort":
                insertionSort(a);
                break;
            case "Quick Sort":
                quickSort(a, 0, a.length - 1);
                break;
        }

        return a;
    }

    private static void bubbleSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - i - 1; j++) {
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }
    }

    private static void selectionSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;

            for (int j = i + 1; j < a.length; j++) {
                if (a[j] < a[min]) {
                    min = j;
                }
            }

            int temp = a[i];
            a[i] = a[min];
            a[min] = temp;
        }
    }

    private static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;

            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }

            a[j + 1] = key;
        }
    }

    private static void quickSort(int[] a, int low, int high) {
        if (low >= high) {
            return;
        }

        int pivot = a[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (a[j] <= pivot) {
                i++;
                int temp = a[i];
                a[i] = a[j];
                a[j] = temp;
            }
        }

        int temp = a[i + 1];
        a[i + 1] = a[high];
        a[high] = temp;

        int pivotIndex = i + 1;

        quickSort(a, low, pivotIndex - 1);
        quickSort(a, pivotIndex + 1, high);
    }

    public static int linearSearch(int[] a, int key) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == key) {
                return i;
            }
        }
        return -1;
    }

    public static int binarySearch(int[] a, int key) {
        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (a[mid] == key) {
                return mid;
            } else if (a[mid] < key) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    public static int[] linearProbing(int[] keys, int size) {
        int[] table = new int[size];
        Arrays.fill(table, -1);

        for (int key : keys) {
            int index = Math.floorMod(key, size);
            int start = index;

            while (table[index] != -1 && table[index] != key) {
                index = (index + 1) % size;

                if (index == start) {
                    break;
                }
            }

            if (table[index] == -1) {
                table[index] = key;
            }
        }

        return table;
    }
}