# Best Time to Buy and Sell Stock

**Source:** [LeetCode 121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) · **Difficulty:** Easy · **Topic:** [Array](../../README.md) · **Patterns:** Single pass with a running minimum (see the [patterns index](../../../../Patterns/README.md))

## 1. Problem Statement

You are given an array `prices`, where `prices[i]` is the price of a stock on day `i`. You may make at most one trade: buy on one day and sell on a strictly later day. Return the largest profit you can make. If every possible trade loses money or breaks even, do not trade and return `0`.

**Constraints:**

- `1 <= prices.length <= 10^5`
- `0 <= prices[i] <= 10^4`

## 2. Examples

**Example 1**

```
Input:  prices = [7, 1, 5, 3, 6, 4]
Output: 5
```

Buy on day 1 at price 1 and sell on day 4 at price 6. Buying at 1 and selling at 7 is not allowed, because the price 7 comes before the price 1.

**Example 2**

```
Input:  prices = [7, 6, 4, 3, 1]
Output: 0
```

The price only falls, so every trade loses money. The best choice is not to trade.

## 3. Pattern Recognition

**What is the problem asking?** Find the largest difference `prices[j] - prices[i]` where `i < j`, or `0` if no such difference is positive.

**What clues are present?**

- We are looking for a pair of positions, and the order of the pair matters: the buy must come before the sell.
- Only one value from the past matters for each sell day: the smallest price before it.
- The input can have 100,000 elements.

**Which patterns should I consider?** Checking every pair, a single pass that remembers the best value seen so far (a running minimum), and seeing it as a maximum subarray problem on daily price changes (Kadane's algorithm).

**Why does the chosen pattern fit?** For a fixed sell day `j`, the best buy day is simply the cheapest day before `j`. That cheapest price can be updated in O(1) as we move from left to right, so one pass gives every sell day its best possible buy. This "carry a summary of the prefix" idea appears in many array problems, and it is also the simplest form of dynamic programming: the answer for the first `j + 1` days is built from the answer for the first `j` days.

**Why are the alternatives less suitable?** Checking every pair works but is O(n²), which is about 5 × 10^9 pairs for the largest input. Sorting the prices loses the order of the days, and the order is the whole constraint. Kadane's algorithm on the daily differences gives the same answer in the same time, but it adds a transformation step that hides the simple idea.

**What assumptions must hold?** Only one trade is allowed, and the buy must happen strictly before the sell. If several trades were allowed, the running minimum alone would not be enough (see the follow-ups).

**How do the constraints influence the choice?** With up to 10^5 prices, an O(n²) solution is too slow, so we need O(n) or O(n log n). Prices are at most 10^4, so every profit fits comfortably in an `int`.

## 4. Prerequisites

- [Array](../../README.md): scanning an array once and tracking values as you go.
- [Complexity analysis in the roadmap](../../../../ROADMAP.md): why O(n²) is too slow for 10^5 elements.

## 5. Intuition

Imagine walking through the days one at a time. On each day, ask: "If I sell today, what is the best profit I could have made?" The answer depends on only one thing from the past, the lowest price so far. You do not need to remember any other earlier price, because buying at a higher price can never give a better profit on the same sell day.

So carry two numbers while you walk: the lowest price seen so far, and the best profit seen so far. Each day, try selling today, then check whether today is the new lowest price for future days.

## 6. Brute-Force Approach

Try every buy day `i` and every later sell day `j`, and keep the largest `prices[j] - prices[i]`.

```
best = 0
for i from 0 to n - 1:
    for j from i + 1 to n - 1:
        best = max(best, prices[j] - prices[i])
return best
```

This is correct, but it checks n(n - 1) / 2 pairs, so it takes O(n²) time and O(1) extra space. For 10^5 prices that is about 5 × 10^9 comparisons, which is far too slow.

## 7. Improved Approach

The brute force repeats work: for every buy day it scans all later days looking for the highest sell price. We can precompute that instead. Build an array `maxAfter`, where `maxAfter[i]` is the highest price from day `i` to the end, by filling it from right to left. Then the best profit when buying on day `i` is `maxAfter[i] - prices[i]`.

This takes O(n) time but O(n) extra space. It is a useful step because it shows the real insight: each day only needs one summary value, not the whole array. Scanning in the other direction, from left to right, the summary we need is the minimum before each day, and that fits in a single variable.

## 8. Preferred Approach

**Core idea:** Scan from left to right, keeping the lowest price seen so far. On each day, the best profit from selling that day is today's price minus that minimum.

**Invariant:** At the start of the loop for day `j`, `minPrice` is the lowest price on days `0` to `j - 1`, and `bestProfit` is the best profit of any trade that sells on or before day `j - 1` (or `0`).

**Why it is correct:** Every valid trade has some sell day `j`. For that sell day, the most profitable buy is the cheapest earlier day, whose price is exactly `minPrice` when the loop reaches `j`. The algorithm computes `prices[j] - minPrice` for every `j`, so it considers the best trade for every sell day and keeps the largest. Starting `bestProfit` at `0` covers the case where no trade is profitable.

**Steps:**

1. Set `minPrice` to `prices[0]` and `bestProfit` to `0`.
2. For each day from 1 to the end:
   1. Update `bestProfit` with `price - minPrice` if that is larger.
   2. Update `minPrice` with `price` if that is smaller.
3. Return `bestProfit`.

The order inside the loop matters. Selling first and then updating the minimum guarantees that the buy day is always strictly before the sell day.

**Trade-offs:** This approach only works for a single trade. It does not tell you which days to trade on unless you also record the indices, which is a small change.

## 9. Dry Run

**Input:** `prices = [7, 1, 5, 3, 6, 4]`

Before the loop, `minPrice = 7` and `bestProfit = 0`.

| Day | Price | `minPrice` before | Profit if sold today | `bestProfit` after | `minPrice` after |
|---|---|---|---|---|---|
| 1 | 1 | 7 | 1 - 7 = -6 | 0 | 1 |
| 2 | 5 | 1 | 5 - 1 = 4 | 4 | 1 |
| 3 | 3 | 1 | 3 - 1 = 2 | 4 | 1 |
| 4 | 6 | 1 | 6 - 1 = 5 | 5 | 1 |
| 5 | 4 | 1 | 4 - 1 = 3 | 5 | 1 |

The result is `5`.

On day 1 the price drops to 1. Selling that day would lose money, so `bestProfit` stays at 0, but 1 becomes the new minimum. From then on every day is compared against 1. The best day to sell is day 4, giving 6 - 1 = 5. Notice that the price 7 on day 0 is never paired with a later sell, because it is never the cheapest earlier price after day 1.

## 10. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
prices = [7, 1, 5, 3, 6, 4] -> max profit = 5
prices = [7, 6, 4, 3, 1] -> max profit = 0
prices = [5] -> max profit = 0
prices = [2, 4, 1] -> max profit = 2
prices = [3, 3, 3] -> max profit = 0
prices = [1, 2, 3, 4, 5] -> max profit = 4
```

Runs on Java 8, 11, 17, 21 and 25.

## 11. Complexity Analysis

- **Time:** O(n), because the loop visits each price once and does a constant amount of work per price.
- **Auxiliary space:** O(1), because only two variables, `minPrice` and `bestProfit`, are kept no matter how long the input is.

## 12. Edge Cases

- **One price,** such as `[5]`. There is no later day to sell on, so the loop does not run and the answer is `0`.
- **Prices only fall,** such as `[7, 6, 4, 3, 1]`. Every profit is negative, so `bestProfit` stays at `0`.
- **All prices equal,** such as `[3, 3, 3]`. Every profit is `0`, so the answer is `0`.
- **A new minimum after the best trade,** such as `[2, 4, 1]`. The minimum changes to 1 on the last day, but the earlier profit of 2 is already stored in `bestProfit` and is not lost.
- **Prices only rise,** such as `[1, 2, 3, 4, 5]`. The best trade is the first day to the last day, giving 4.

## 13. Common Mistakes

- **Returning the maximum minus the minimum of the whole array.** For `[7, 1, 5, 3, 6, 4]` this gives 7 - 1 = 6, but the 7 comes before the 1, so that trade is impossible. The order of the days matters.
- **Starting `bestProfit` at a negative value or at `prices[1] - prices[0]`.** The problem says to return `0` when no trade is profitable, so the starting value must be `0`.
- **Starting `minPrice` at `0`.** Prices can be greater than 0, so a minimum of 0 makes every price look like a profit. Start with the first price, or with `Integer.MAX_VALUE` and loop from day 0.
- **Resetting `bestProfit` when a new minimum appears.** A lower price later in the array does not cancel a profit you could already have made.

## 14. Interview Follow-ups

- **Return the buy and sell days, not just the profit.** Keep the index of the current minimum, and record both indices whenever `bestProfit` improves.
- **Unlimited trades,** where you can buy and sell many times but hold at most one share at a time ([LeetCode 122](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/)). The answer becomes the sum of every positive day-to-day increase, which is a greedy idea.
- **At most two trades, or at most k trades** ([LeetCode 123](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/) and [LeetCode 188](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/)). A single running minimum is no longer enough, and the usual solution is dynamic programming with states for each trade.
- **A cooldown day after selling, or a fee per trade** ([LeetCode 309](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) and [LeetCode 714](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/)). These are also solved with a small dynamic programming state machine, such as "holding a share" and "not holding a share".
- **Prices arrive as a live stream.** The same two variables work unchanged, because the algorithm never looks back at earlier prices.

## 15. Revision Summary

For a single buy and a later sell, the best trade for each sell day uses the cheapest earlier price. Scan once from left to right, keep the running minimum and the best profit, and sell before updating the minimum so the buy always comes first. Start the profit at 0 so that "no trade" is the answer when prices only fall. This takes O(n) time and O(1) extra space, and it only works because exactly one trade is allowed.
