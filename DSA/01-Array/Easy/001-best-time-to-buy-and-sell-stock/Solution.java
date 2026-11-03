import java.util.Arrays;

public class Solution {

    /**
     * Returns the largest profit from buying on one day and selling on a later day,
     * or 0 if no trade makes a profit.
     */
    public static int maxProfit(int[] prices) {
        // The problem guarantees at least one price, but an empty input simply has no trade.
        if (prices == null || prices.length == 0) {
            return 0;
        }

        int minPrice = prices[0]; // cheapest buy price seen so far
        int bestProfit = 0;       // 0 means "do not trade"

        for (int day = 1; day < prices.length; day++) {
            int price = prices[day];

            // Sell today, using the cheapest day before today as the buy day.
            bestProfit = Math.max(bestProfit, price - minPrice);

            // Update the minimum only after selling, so we never buy and sell on the same day.
            minPrice = Math.min(minPrice, price);
        }

        return bestProfit;
    }

    public static void main(String[] args) {
        int[][] testCases = {
            {7, 1, 5, 3, 6, 4},
            {7, 6, 4, 3, 1},
            {5},
            {2, 4, 1},
            {3, 3, 3},
            {1, 2, 3, 4, 5}
        };

        for (int[] prices : testCases) {
            System.out.println("prices = " + Arrays.toString(prices) + " -> max profit = " + maxProfit(prices));
        }
    }
}
