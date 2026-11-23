import java.util.Arrays;

public class Solution {

    /**
     * Returns the largest amount of water a container can hold, where the container
     * is formed by two lines height[i] and height[j] (i < j) and the x-axis.
     * Its area is min(height[i], height[j]) * (j - i). The input array is not changed.
     */
    public static int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int best = 0;

        while (left < right) {
            int width = right - left;
            int area = Math.min(height[left], height[right]) * width;
            best = Math.max(best, area);

            // Move the shorter line. Every other container that uses the shorter line
            // pairs it with a line between the pointers, so it is narrower and still
            // no taller than the shorter line: its area cannot beat the one just
            // recorded. The shorter line can be discarded for good. On a tie, either
            // line can be discarded by the same argument, so moving right is fine.
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return best;
    }

    /** Brute force for comparison: checks every pair of lines in O(n^2) time. */
    public static int maxAreaEveryPair(int[] height) {
        int best = 0;
        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                best = Math.max(best, Math.min(height[i], height[j]) * (j - i));
            }
        }
        return best;
    }

    public static void main(String[] args) {
        int[][] testCases = {
            {1, 8, 6, 2, 5, 4, 8, 3, 7},
            {1, 1},
            {3, 3, 1},
            {4, 3, 2, 1, 4},
            {1, 2, 4, 3},
            {0, 0, 0}
        };

        for (int[] height : testCases) {
            System.out.println("height = " + Arrays.toString(height)
                    + " -> two pointers: " + maxArea(height)
                    + ", every pair: " + maxAreaEveryPair(height));
        }

        // The largest allowed input: 10^5 lines of height 10^4. The best container uses
        // the two outer lines, with area 10^4 * (10^5 - 1) = 999,990,000, which fits in an int.
        // Checking every pair here would take about 5 * 10^9 steps, so only the scan runs.
        int[] tallest = new int[100000];
        Arrays.fill(tallest, 10000);
        System.out.println("height = 100000 lines of height 10000 -> two pointers: " + maxArea(tallest));
    }
}
