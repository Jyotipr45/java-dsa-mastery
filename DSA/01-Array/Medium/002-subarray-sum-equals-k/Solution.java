import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {

    /**
     * Returns the number of contiguous, non-empty subarrays of nums whose sum is
     * exactly k. Works with negative values and with k = 0. The input array is not
     * changed.
     */
    public static int subarraySum(int[] nums, int k) {
        // seen.get(s) is how many prefixes seen so far have sum s.
        // The empty prefix (sum 0) is there from the start, so a subarray that
        // begins at index 0 can be counted.
        Map<Integer, Integer> seen = new HashMap<>();
        seen.put(0, 1);

        // With n <= 2 * 10^4 and |nums[i]| <= 1000, every prefix sum is within
        // -2 * 10^7 to 2 * 10^7 and running - k within -3 * 10^7 to 3 * 10^7,
        // so int is safe.
        int running = 0;
        int count = 0;

        for (int value : nums) {
            running += value;
            // Every earlier prefix with sum running - k starts a subarray that
            // ends here and sums to k. Look up before recording the current
            // prefix, so a prefix is never paired with itself (an empty subarray).
            count += seen.getOrDefault(running - k, 0);
            seen.put(running, seen.getOrDefault(running, 0) + 1);
        }

        return count;
    }

    private static void show(int[] nums, int k) {
        System.out.println("nums = " + Arrays.toString(nums) + ", k = " + k
                + " -> " + subarraySum(nums, k));
    }

    public static void main(String[] args) {
        show(new int[] {1, 1, 1}, 2);
        show(new int[] {1, 2, 3}, 3);
        show(new int[] {3, 4, 7, 2, -3, 1, 4, 2}, 7);
        show(new int[] {1, -1, 5}, 5);
        show(new int[] {1, -1, 1, -1}, 0);
        show(new int[] {5}, 3);

        // The largest inputs the constraints allow.
        int[] zeros = new int[20000];
        System.out.println("nums = 20000 zeros, k = 0 -> " + subarraySum(zeros, 0));

        int[] thousands = new int[20000];
        Arrays.fill(thousands, 1000);
        System.out.println("nums = 20000 copies of 1000, k = 10000000 -> "
                + subarraySum(thousands, 10000000));
    }
}
