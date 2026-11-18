import java.util.Arrays;

public class Solution {

    /**
     * Returns the length of the shortest contiguous subarray of nums whose sum is
     * at least target, or 0 if no such subarray exists.
     * Assumes every value in nums is positive. The input array is not changed.
     */
    public static int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int windowSum = 0;
        int shortest = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            // Grow the window: nums[right] enters on the right.
            windowSum += nums[right];

            // While the window's sum reaches target, record its length and drop
            // nums[left] to try a shorter window. Positive values make this safe:
            // removing an element can only lower the sum, so once the sum is below
            // target, no shorter window ending at right can reach it.
            while (windowSum >= target) {
                shortest = Math.min(shortest, right - left + 1);
                windowSum -= nums[left];
                left++;
            }
        }

        return shortest == Integer.MAX_VALUE ? 0 : shortest;
    }

    /**
     * The O(n log n) follow-up: build prefix sums, then for each start index binary
     * search for the first end whose prefix sum reaches prefix[start] + target.
     * Also assumes positive values, which make the prefix sums strictly increasing.
     */
    public static int minSubArrayLenBinarySearch(int target, int[] nums) {
        int n = nums.length;
        // prefix[i] is the sum of nums[0..i - 1]. With n <= 10^5 and nums[i] <= 10^4,
        // the largest prefix sum is 10^9, which fits in an int.
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        int shortest = Integer.MAX_VALUE;
        for (int start = 0; start < n; start++) {
            // Here prefix[start] + target stays below 2 * 10^9, just under Integer.MAX_VALUE.
            // Using long keeps the comparison correct even if the limits were larger.
            long needed = (long) prefix[start] + target;
            int end = firstAtLeast(prefix, start + 1, n, needed);
            if (end <= n) {
                shortest = Math.min(shortest, end - start);
            }
        }
        return shortest == Integer.MAX_VALUE ? 0 : shortest;
    }

    // Returns the smallest index i in [low, high] with prefix[i] >= needed, or high + 1 if none.
    private static int firstAtLeast(int[] prefix, int low, int high, long needed) {
        int answer = high + 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (prefix[mid] >= needed) {
                answer = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return answer;
    }

    public static void main(String[] args) {
        int[] targets = {7, 4, 11, 15, 6};
        int[][] testCases = {
            {2, 3, 1, 2, 4, 3},
            {1, 4, 4},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 3, 4, 5},
            {10, 2, 3}
        };

        for (int t = 0; t < testCases.length; t++) {
            System.out.println("target = " + targets[t] + ", nums = " + Arrays.toString(testCases[t])
                    + " -> window: " + minSubArrayLen(targets[t], testCases[t])
                    + ", binary search: " + minSubArrayLenBinarySearch(targets[t], testCases[t]));
        }

        // The largest allowed input: 10^5 copies of 10^4 add up to exactly 10^9,
        // the largest allowed target, so the whole array is needed.
        int[] largest = new int[100000];
        Arrays.fill(largest, 10000);
        System.out.println("target = 1000000000, nums = 100000 copies of 10000 -> window: "
                + minSubArrayLen(1000000000, largest)
                + ", binary search: " + minSubArrayLenBinarySearch(1000000000, largest));

        int[] ones = new int[100000];
        Arrays.fill(ones, 1);
        System.out.println("target = 1000000000, nums = 100000 copies of 1 -> window: "
                + minSubArrayLen(1000000000, ones)
                + ", binary search: " + minSubArrayLenBinarySearch(1000000000, ones));
    }
}
