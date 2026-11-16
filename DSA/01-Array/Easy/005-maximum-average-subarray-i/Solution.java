import java.util.Arrays;

public class Solution {

    /**
     * Returns the largest average of any k consecutive elements of nums.
     * Assumes 1 <= k <= nums.length. The input array is not changed.
     */
    public static double findMaxAverage(int[] nums, int k) {
        // The first window holds nums[0..k - 1].
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }

        // Start from the first window, not from 0, because every average may be negative.
        int maxSum = windowSum;
        for (int right = k; right < nums.length; right++) {
            // Slide by one: nums[right] enters and nums[right - k] leaves.
            windowSum += nums[right] - nums[right - k];
            maxSum = Math.max(maxSum, windowSum);
        }

        // Every window has the same length k, so the largest sum gives the largest
        // average. Compare whole-number sums and divide only once at the end.
        // With k <= 10^5 and |nums[i]| <= 10^4, every sum is at most 10^9 in absolute
        // value, which fits in an int. For larger inputs, use long.
        return (double) maxSum / k;
    }

    public static void main(String[] args) {
        int[][] testCases = {
            {1, 12, -5, -6, 50, 3},
            {5},
            {-1, -2, -3},
            {0, 4, 0, 3, 2},
            {3, 3, 4, 3, 0},
            {4, -1, 2}
        };
        int[] windowSizes = {4, 1, 2, 1, 3, 3};

        for (int t = 0; t < testCases.length; t++) {
            System.out.println("nums = " + Arrays.toString(testCases[t]) + ", k = " + windowSizes[t]
                    + " -> maximum average = " + findMaxAverage(testCases[t], windowSizes[t]));
        }

        // The largest allowed input: 10^5 copies of 10^4 in one window of size 10^5.
        // The window sum is 10^9, still below Integer.MAX_VALUE (2147483647).
        int[] largest = new int[100000];
        Arrays.fill(largest, 10000);
        System.out.println("nums = 100000 copies of 10000, k = 100000 -> maximum average = "
                + findMaxAverage(largest, 100000));

        int[] smallest = new int[100000];
        Arrays.fill(smallest, -10000);
        System.out.println("nums = 100000 copies of -10000, k = 100000 -> maximum average = "
                + findMaxAverage(smallest, 100000));
    }
}
