import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {

    /**
     * Returns the indexes of two different elements of nums whose values add up to
     * target, smaller index first, or an empty array if no such pair exists.
     * The input array is not changed.
     */
    public static int[] twoSum(int[] nums, int target) {
        // Maps each value seen so far to the index where it was seen.
        Map<Integer, Integer> indexOfValue = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            // With values and target between -10^9 and 10^9, the complement is
            // between -2 * 10^9 and 2 * 10^9, which still fits in an int.
            int complement = target - nums[i];

            // Look up the partner before storing nums[i], so an element is never
            // paired with itself. Any partner found here has a smaller index.
            Integer partnerIndex = indexOfValue.get(complement);
            if (partnerIndex != null) {
                return new int[] {partnerIndex, i};
            }

            indexOfValue.put(nums[i], i);
        }

        // The problem guarantees an answer, but return an empty array if there is none.
        return new int[0];
    }

    public static void main(String[] args) {
        int[][] testCases = {
            {4, 9, 1, 6},
            {3, 5, 3},
            {5, 7},
            {-3, 8, 2, -5},
            {0, 4, 0},
            {1000000000, -999999999, -1},
            {1, 2, 3}
        };
        int[] targets = {7, 6, 12, -8, 0, -1000000000, 100};

        for (int t = 0; t < testCases.length; t++) {
            int[] nums = testCases[t];
            System.out.println("nums = " + Arrays.toString(nums)
                    + ", target = " + targets[t]
                    + " -> " + Arrays.toString(twoSum(nums, targets[t])));
        }
    }
}
