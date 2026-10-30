import java.util.Arrays;

public class Solution {

    /**
     * Returns a new array where position i holds nums[0] + nums[1] + ... + nums[i].
     * The input array is not changed.
     */
    public static int[] runningSum(int[] nums) {
        // The problem guarantees at least one number, but an empty input has an empty answer.
        if (nums == null || nums.length == 0) {
            return new int[0];
        }

        int[] result = new int[nums.length];
        int total = 0; // sum of nums[0..i - 1] at the start of each step

        for (int i = 0; i < nums.length; i++) {
            // Reuse the sum of everything before i instead of adding it up again.
            total += nums[i];
            result[i] = total;
        }

        // With at most 1000 values of size 10^6, |total| <= 10^9, which fits in an int.
        // For larger inputs, use long for total and the result.
        return result;
    }

    public static void main(String[] args) {
        int[][] testCases = {
            {1, 2, 3, 4},
            {1, 1, 1, 1, 1},
            {3, 1, 2, 10, 1},
            {5},
            {-2, 5, -1, -3}
        };

        for (int[] nums : testCases) {
            System.out.println("nums = " + Arrays.toString(nums)
                    + " -> running sum = " + Arrays.toString(runningSum(nums)));
        }

        // The largest input the constraints allow: 1000 copies of 10^6.
        int[] largest = new int[1000];
        Arrays.fill(largest, 1_000_000);
        int[] sums = runningSum(largest);
        System.out.println("1000 copies of 1000000 -> last running sum = " + sums[sums.length - 1]);
    }
}
