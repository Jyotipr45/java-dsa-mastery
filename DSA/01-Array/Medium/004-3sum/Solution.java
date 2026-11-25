import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Solution {

    /**
     * Returns every unique triplet [a, b, c] of values from nums, taken from three
     * different indexes, with a + b + c == 0. Each triplet is in non-decreasing order,
     * and the triplets are in lexicographic order. The input array is not changed.
     */
    public static List<List<Integer>> threeSum(int[] nums) {
        int[] sorted = Arrays.copyOf(nums, nums.length);
        Arrays.sort(sorted);
        int n = sorted.length;
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < n - 2; i++) {
            // The other two values are at least sorted[i], so if it is positive,
            // no triplet from here on can sum to zero.
            if (sorted[i] > 0) {
                break;
            }
            // The same first value would find exactly the same triplets again.
            if (i > 0 && sorted[i] == sorted[i - 1]) {
                continue;
            }

            int left = i + 1;
            int right = n - 1;
            while (left < right) {
                // Each value is at most 10^5 in size, so the sum fits in an int.
                int sum = sorted[i] + sorted[left] + sorted[right];
                if (sum < 0) {
                    left++;   // sorted[left] is too small to work with any remaining right
                } else if (sum > 0) {
                    right--;  // sorted[right] is too large to work with any remaining left
                } else {
                    result.add(Arrays.asList(sorted[i], sorted[left], sorted[right]));
                    left++;
                    right--;
                    // Skip copies of the values just used, so the same triplet
                    // is not recorded again.
                    while (left < right && sorted[left] == sorted[left - 1]) {
                        left++;
                    }
                    while (left < right && sorted[right] == sorted[right + 1]) {
                        right--;
                    }
                }
            }
        }
        return result;
    }

    /**
     * The same scan with every duplicate skip removed. It finds the right triplets
     * but can record some of them more than once. Kept only to show why the skips exist.
     */
    public static List<List<Integer>> threeSumWithoutSkips(int[] nums) {
        int[] sorted = Arrays.copyOf(nums, nums.length);
        Arrays.sort(sorted);
        int n = sorted.length;
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < n - 2; i++) {
            int left = i + 1;
            int right = n - 1;
            while (left < right) {
                int sum = sorted[i] + sorted[left] + sorted[right];
                if (sum < 0) {
                    left++;
                } else if (sum > 0) {
                    right--;
                } else {
                    result.add(Arrays.asList(sorted[i], sorted[left], sorted[right]));
                    left++;
                    right--;
                }
            }
        }
        return result;
    }

    /**
     * Brute force for comparison: checks every triple of indexes in O(n^3) time.
     * Sorting first makes each triplet come out in non-decreasing order, so a set
     * can remove repeats, and the triplets come out in the same order as threeSum.
     */
    public static List<List<Integer>> threeSumEveryTriple(int[] nums) {
        int[] sorted = Arrays.copyOf(nums, nums.length);
        Arrays.sort(sorted);
        Set<List<Integer>> unique = new LinkedHashSet<>();
        for (int i = 0; i < sorted.length; i++) {
            for (int j = i + 1; j < sorted.length; j++) {
                for (int k = j + 1; k < sorted.length; k++) {
                    if (sorted[i] + sorted[j] + sorted[k] == 0) {
                        unique.add(Arrays.asList(sorted[i], sorted[j], sorted[k]));
                    }
                }
            }
        }
        return new ArrayList<>(unique);
    }

    public static void main(String[] args) {
        int[][] testCases = {
            {-1, 0, 1, 2, -1, -4},
            {0, 1, 1},
            {0, 0, 0},
            {0, 0, 0, 0},
            {-2, 0, 0, 2, 2},
            {-4, -2, -2, -2, 0, 1, 2, 2, 2, 3, 3, 4, 4, 6, 6},
            {1, 2, -2, -1}
        };

        for (int[] nums : testCases) {
            List<List<Integer>> twoPointers = threeSum(nums);
            System.out.println("nums = " + Arrays.toString(nums)
                    + " -> " + twoPointers
                    + ", matches every triple: " + twoPointers.equals(threeSumEveryTriple(nums)));
        }

        // Without the skips, the same triplet can be recorded several times.
        System.out.println("without skips, [-1, 0, 1, 2, -1, -4] -> "
                + threeSumWithoutSkips(new int[] {-1, 0, 1, 2, -1, -4}));
        System.out.println("without skips, [-2, 0, 0, 2, 2] -> "
                + threeSumWithoutSkips(new int[] {-2, 0, 0, 2, 2}));

        // The largest allowed input with many duplicates: 3000 zeros. The answer is a
        // single triplet. Checking every triple would take about 4.5 * 10^9 steps,
        // so only the two-pointer scan runs here.
        int[] zeros = new int[3000];
        System.out.println("nums = 3000 zeros -> " + threeSum(zeros));
    }
}
