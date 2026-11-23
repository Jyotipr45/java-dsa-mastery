# Container With Most Water

**Source:** [LeetCode 11](https://leetcode.com/problems/container-with-most-water/) · **Difficulty:** Medium · **Topic:** [Array](../../README.md) · **Patterns:** [Two Pointers](../../../../Patterns/TwoPointers.md)

## 1. Problem Statement

You are given an array `height` of `n` non-negative integers. Picture `n` vertical lines standing on the x-axis, where line `i` is at position `i` and has height `height[i]`. Choose two lines. Together with the x-axis they form a container, and the water it holds is limited by the shorter line, so its area is `min(height[i], height[j]) * (j - i)`. Return the largest area any pair of lines can hold. The container cannot be tilted.

**Constraints:**

- `n == height.length`
- `2 <= n <= 10^5`
- `0 <= height[i] <= 10^4`

## 2. Examples

**Example 1**

```
Input:  height = [1, 8, 6, 2, 5, 4, 8, 3, 7]
Output: 49
```

The lines at index 1 (height 8) and index 8 (height 7) are 7 apart. The shorter one is 7, so the area is `7 * 7 = 49`.

**Example 2**

```
Input:  height = [1, 1]
Output: 1
```

There is only one pair. It is 1 wide and 1 tall.

## 3. Pattern Recognition

**What is the problem asking?** Among all pairs of indexes `i < j`, find the largest value of `min(height[i], height[j]) * (j - i)`.

**What clues are present?**

- The answer depends on a pair of positions, and a brute-force solution checks every pair `(i, j)`.
- One factor of the area, the width `j - i`, is largest when the two indexes are at opposite ends and only shrinks as they move towards each other.
- The other factor is the minimum of the two heights, so the shorter line alone sets the height.
- The input is not sorted, and sorting would destroy the positions that define the width.

**Which patterns should I consider?** Checking every pair, and opposite-end [Two Pointers](../../../../Patterns/TwoPointers.md). Hashing does not help, because there is no exact value to look up, and sorting is ruled out because positions matter.

**Why does the chosen pattern fit?** The Two Pointers guide explains that the opposite-ends form works when each step can rule out one element for good. Usually sorted order gives that guarantee. Here a different argument gives it. Start with the widest container, `left = 0` and `right = n - 1`, and say `height[left]` is the shorter line. Any other container that uses `left` pairs it with some line between the pointers. That container is narrower, and its height is still at most `height[left]`, because the shorter line caps it. So none of them can beat the area just recorded, and `left` can be dropped without missing the answer. The same holds for `right` when it is the shorter line. Each step drops one line, so one pass is enough.

**Why are the alternatives less suitable?** Checking every pair is correct but O(n²), about 5 × 10^9 pairs for `n = 10^5`. Moving the taller line instead of the shorter one looks similar but is wrong: the area can never grow while the shorter line stays, because the height is still capped by it and the width has shrunk. For Example 1, a version that moves the taller line returns 8 instead of 49 (checked by running it). It records `[0..8]` with area 8, then drops the line of height 7 and keeps the line of height 1, which caps every container it tries afterwards.

**What assumptions must hold?** The area must be the shorter height times the distance. That is what makes the shorter line the one that can be discarded. Heights can be zero, and the argument still holds. If the problem changed the area formula, for example to count water trapped above every bar between the lines (LeetCode 42), the argument no longer applies as written.

**How do the constraints influence the choice?** With `n` up to 10^5, O(n²) is too slow and O(n) is needed in practice. The constraints also settle the number type. The largest possible area is `10^4 * (10^5 - 1) = 999,990,000`, which fits in an `int` (the limit is 2,147,483,647). The sample prints that case.

## 4. Prerequisites

- [Array](../../README.md): indexes and scanning an array.
- [Two Pointers](../../../../Patterns/TwoPointers.md): the opposite-ends form and why each step must rule out one element for good.
- [Valid Palindrome](../../../02-String/Easy/002-valid-palindrome/README.md) (String, Easy 002): another opposite-end scan on input that is not sorted.

## 5. Intuition

The widest container uses the two outer lines. Every other container is narrower, so to beat it, a container must be taller. The height is set by the shorter line, so the only hope of getting taller is to replace the shorter line. Replacing the taller one cannot help: the shorter line still caps the height, and the width only goes down.

So start wide and, at each step, record the area and then give up the shorter line, hoping to find a taller one further in. You trade width for a chance at height, and you never throw away a line that could still be part of a better container.

## 6. Brute-Force Approach

Compute the area of every pair and keep the largest.

```
best = 0
for i from 0 to n - 1:
    for j from i + 1 to n - 1:
        best = max(best, min(height[i], height[j]) * (j - i))
return best
```

This uses O(1) extra space and needs no special insight, but it checks `n(n - 1) / 2` pairs, so the time is O(n²). It is implemented as `maxAreaEveryPair` in [Solution.java](Solution.java), and the program uses it to check the small examples.

## 7. Preferred Approach: Move the Shorter Line

**Core idea:** Put `left` at the first line and `right` at the last. Record the area, then move whichever pointer is at the shorter line one step inwards. Stop when the pointers meet.

**Invariant:** Every pair of lines that uses an index outside `[left, right]` has an area no larger than `best`. So the answer is either `best` or the area of some pair inside `[left, right]`.

**Why it is correct:** At the start, no index is outside the range, so the invariant holds. Suppose `height[left] <= height[right]` and the code moves `left`. Any pair `(left, j)` with `left < j < right` has width `j - left`, which is less than `right - left`, and height `min(height[left], height[j])`, which is at most `height[left]`. So its area is at most `height[left] * (right - left)`, the area just recorded and included in `best`. Pairs of `left` with indexes outside the range are already covered. So every pair that uses `left` is covered, and the invariant still holds after `left` moves. The case where `right` is shorter is the same with the roles swapped. On a tie, the code moves `right`; the same argument works for either pointer, because both lines are the shorter one. When `left == right`, no pairs remain inside the range, so `best` is the answer.

**Steps:**

1. Set `left = 0`, `right = n - 1` and `best = 0`.
2. While `left < right`:
   1. Compute `area = min(height[left], height[right]) * (right - left)` and update `best`.
   2. If `height[left] < height[right]`, add 1 to `left`. Otherwise subtract 1 from `right`.
3. Return `best`.

**Trade-offs:** This is O(n) time and O(1) space, and it is hard to beat for this problem. Its weakness is that it is easy to get the direction wrong and hard to see why it works. If you cannot explain why the shorter line is safe to drop, an interviewer will usually ask, so practise the argument above rather than memorising the code.

## 8. Dry Run

**Input:** `height = [1, 8, 6, 2, 5, 4, 8, 3, 7]`

| Step | `left` | `right` | `height[left]` | `height[right]` | Width | Area | `best` | Move |
|---|---|---|---|---|---|---|---|---|
| 1 | 0 | 8 | 1 | 7 | 8 | 8 | 8 | left (1 < 7) |
| 2 | 1 | 8 | 8 | 7 | 7 | 49 | 49 | right (7 is shorter) |
| 3 | 1 | 7 | 8 | 3 | 6 | 18 | 49 | right |
| 4 | 1 | 6 | 8 | 8 | 5 | 40 | 49 | right (tie) |
| 5 | 1 | 5 | 8 | 4 | 4 | 16 | 49 | right |
| 6 | 1 | 4 | 8 | 5 | 3 | 15 | 49 | right |
| 7 | 1 | 3 | 8 | 2 | 2 | 4 | 49 | right |
| 8 | 1 | 2 | 8 | 6 | 1 | 6 | 49 | right |

After step 8, `right` becomes 1, which equals `left`, so the loop ends and the method returns 49.

Step 1 drops the line of height 1 at once: no container that uses it can be taller than 1, and none is wider than the one just recorded. From step 2 on, the line of height 8 at index 1 is never the shorter one, except for the tie in step 4, so `right` keeps moving. The answer appears in step 2, and the remaining steps confirm that nothing inside is better. Eight steps for nine lines: each step drops exactly one line.

## 9. Java Solution

See [Solution.java](Solution.java). `maxArea` is the two-pointer scan and `maxAreaEveryPair` is the brute force, used to check the small cases. To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
height = [1, 8, 6, 2, 5, 4, 8, 3, 7] -> two pointers: 49, every pair: 49
height = [1, 1] -> two pointers: 1, every pair: 1
height = [3, 3, 1] -> two pointers: 3, every pair: 3
height = [4, 3, 2, 1, 4] -> two pointers: 16, every pair: 16
height = [1, 2, 4, 3] -> two pointers: 4, every pair: 4
height = [0, 0, 0] -> two pointers: 0, every pair: 0
height = 100000 lines of height 10000 -> two pointers: 999990000
```

The last line uses the largest input the constraints allow. The best container uses the two outer lines, and its area, 999,990,000, fits in an `int`. The brute force is not run there, because it would check about 5 × 10^9 pairs.

Runs on Java 8, 11, 17, 21 and 25.

## 10. Complexity Analysis

- **Time:** O(n). Each step moves one pointer one place inwards, and the loop stops when they meet, so it runs exactly `n - 1` times, each in constant time.
- **Auxiliary space:** O(1): `left`, `right` and `best`. The brute force also uses O(1) space but O(n²) time.

## 11. Edge Cases

- **Only two lines,** such as Example 2. The loop runs once and returns their area.
- **The best pair is the two outer lines,** such as `[4, 3, 2, 1, 4]`. It is recorded in the first step, before anything moves.
- **The best pair is in the middle,** such as `[1, 2, 4, 3]`, where the pair `(1, 3)` gives `min(2, 3) * 2 = 4` while the outer pair gives only 3. The scan records 3, drops the line of height 1 and finds 4 in the next step.
- **Equal heights,** such as `[3, 3, 1]` or step 4 of the dry run. Moving either pointer is safe.
- **Zero heights,** such as `[0, 0, 0]`. Every area is 0, and the method returns 0.
- **The largest input,** 10^5 lines of height 10^4. The area stays below 10^9, so `int` is safe.

## 12. Common Mistakes

- **Moving the taller line.** It looks reasonable, but the shorter line still caps the height and the width shrinks, so the area cannot grow. For Example 1 that version returns 8 instead of 49, and for `[3, 3, 1]` it returns 2 instead of 3.
- **Using the taller line for the height,** or the average of the two. Water spills over the shorter line, so the height is `min(height[left], height[right])`.
- **Measuring the width as `right - left + 1`.** The lines have no thickness, so lines at positions 1 and 8 are 7 apart, not 8.
- **Sorting the heights first.** Sorting loses the positions, and the width depends on them.
- **Moving both pointers at once on a tie.** It happens to be safe here, because on a tie neither line can be part of a larger container with a line between the pointers, but it is easy to get wrong in similar problems. Moving one pointer per step is simpler to reason about.

## 13. Interview Follow-ups

- **Return the two indexes.** Store `left` and `right` whenever `best` improves. The cost stays O(n) time and O(1) space.
- **Skip lines that cannot help.** After moving past a shorter line, you can keep moving while the new line is no taller than the one you just dropped, because it cannot do better with a smaller width. This saves steps in practice but does not change the O(n) bound.
- **Trapping Rain Water** ([LeetCode 42](https://leetcode.com/problems/trapping-rain-water/), Hard). Every bar holds water and the bars between the lines matter. It also has an opposite-end two-pointer solution, with a different argument: the side with the smaller maximum so far decides how much water the current bar holds.
- **What if the area formula changes?** Ask whether the shorter element still caps every container that uses it. If it does, the same argument applies. If it does not, fall back to checking pairs or look for another structure.

## 14. Revision Summary

The area of a container is the shorter height times the distance. Start with the widest container and, at each step, record its area and drop the shorter line: every other container that uses it is narrower and no taller, so it can never win. This is opposite-end [Two Pointers](../../../../Patterns/TwoPointers.md) on unsorted input, where this argument replaces the usual need for sorted order. It runs in O(n) time with O(1) auxiliary space, and with these constraints the area fits in an `int`.
