import java.util.Arrays;

public class Solution {

    /**
     * Returns the leftmost index i where the sum of the elements to the left of i
     * equals the sum of the elements to the right of i, or -1 if there is none.
     * The element at i belongs to neither side. The input array is not changed.
     */
    public static int pivotIndex(int[] nums) {
        // First pass: the sum of the whole array.
        int total = 0;
        for (int num : nums) {
            total += num;
        }

        // Second pass: leftSum is the sum of nums[0..i - 1] at the start of each step.
        int leftSum = 0;
        for (int i = 0; i < nums.length; i++) {
            // Everything that is not on the left and is not nums[i] is on the right.
            int rightSum = total - leftSum - nums[i];
            if (leftSum == rightSum) {
                return i; // scanning from the left, so the first match is the leftmost
            }
            leftSum += nums[i];
        }

        // With at most 10^4 values of size 1000, every sum is at most 10^7 in absolute
        // value, which fits in an int. For larger inputs, use long.
        return -1;
    }

    public static void main(String[] args) {
        int[][] testCases = {
            {2, 3, -1, 8, 4},
            {1, 2, 3},
            {5, 4, -4},
            {1, -1, 0},
            {0, 0, 0},
            {7},
            {-1, -1, -1, -1, 0, 1}
        };

        for (int[] nums : testCases) {
            System.out.println("nums = " + Arrays.toString(nums)
                    + " -> pivot index = " + pivotIndex(nums));
        }
    }
}
