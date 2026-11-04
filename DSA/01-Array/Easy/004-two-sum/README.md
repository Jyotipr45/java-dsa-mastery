# Two Sum

**Source:** [LeetCode 1](https://leetcode.com/problems/two-sum/) · **Difficulty:** Easy · **Topic:** [Array](../../README.md), [Hashing](../../../03-Hashing/README.md) · **Patterns:** Hashing (complement lookup), compared with [Two Pointers](../../../../Patterns/TwoPointers.md)

## 1. Problem Statement

You are given an integer array `nums` and an integer `target`. Find two different positions `i` and `j` such that `nums[i] + nums[j]` equals `target`, and return their indexes. The same element cannot be used twice, although two different elements may hold the same value. The input is guaranteed to have exactly one such pair, and the indexes may be returned in any order.

**Constraints:**

- `2 <= nums.length <= 10^4`
- `-10^9 <= nums[i] <= 10^9`
- `-10^9 <= target <= 10^9`
- Exactly one valid answer exists.

## 2. Examples

**Example 1**

```
Input:  nums = [4, 9, 1, 6], target = 7
Output: [2, 3]
```

`nums[2] + nums[3] = 1 + 6 = 7`. No other pair adds up to 7.

**Example 2**

```
Input:  nums = [3, 5, 3], target = 6
Output: [0, 2]
```

The two 3s are different elements, so they form a valid pair. Using index 0 twice is not allowed.

**Example 3**

```
Input:  nums = [-3, 8, 2, -5], target = -8
Output: [0, 3]
```

Negative values work the same way: `-3 + (-5) = -8`.

## 3. Pattern Recognition

**What is the problem asking?** Among all pairs of positions, find the one whose values add up to `target`, and report where those values are in the original array.

**What clues are present?**

- The condition involves a pair of elements, which suggests a brute force over every pair, O(n²), that we want to beat.
- Once one value `x` is fixed, its partner is fully determined: it must be `target - x`. The question for every element becomes "have I already seen the value I need?"
- The array is not sorted, and the answer is a pair of original indexes, not a pair of values.

**Which patterns should I consider?** Checking every pair, sorting and then using [Two Pointers](../../../../Patterns/TwoPointers.md) from both ends, and a hash map from value to index that answers "is the partner here?" in O(1) on average, as described in the [Hashing](../../../03-Hashing/README.md) guide.

**Why does the chosen pattern fit?** The clue "is the partner I need somewhere?" is exactly the kind of question a hash map answers. Because the partner of `x` is a single known value, one lookup replaces the whole inner loop of the brute force. Storing each value with its index also keeps the original positions, which the answer needs.

**Why are the alternatives less suitable?** Checking every pair is simple but does about n²/2 additions. Two pointers needs sorted input. Sorting moves the elements away from their original positions, so the indexes have to be carried along in a separate array, which costs O(n log n) time and O(n) extra space. That is slower than hashing and no cheaper in memory. Two pointers is the better choice when the input is already sorted, because then it needs no extra memory at all.

**What assumptions must hold?** The two positions must be different, but their values may be equal. Values can be negative or zero. The sums must not overflow the number type used. Here every complement `target - nums[i]` lies between -2 × 10^9 and 2 × 10^9, which fits in an `int` (the limit is about ±2.147 × 10^9), so `int` is safe, with little room to spare.

**How do the constraints influence the choice?** With up to 10^4 elements, the brute force does about 5 × 10^7 pair checks in the worst case. That runs, but it is the kind of cost that grows badly, and the problem's own follow-up asks for something faster than O(n²). The values are as large as 10^9 and can be negative, so a lookup array indexed by value is impossible, and a hash map is the practical way to look up values directly.

## 4. Prerequisites

- [Array](../../README.md): scanning an array from left to right with an index.
- [Hashing](../../../03-Hashing/README.md): how `HashMap` stores keys, why `get` and `put` are O(1) on average, and the boxing pitfalls of `HashMap<Integer, Integer>`.
- [Two Pointers](../../../../Patterns/TwoPointers.md): the pair-sum search on sorted input, used here as the alternative.

## 5. Intuition

Imagine walking along the array and writing each number you pass in a notebook, together with its position. When you reach a new number `x`, you do not need to compare it with every earlier number. You know exactly which number would complete the pair, `target - x`, so you only check whether that one number is already in the notebook.

If it is, the earlier position and the current position are the answer. If it is not, write `x` down and keep walking. A pair is always found when you reach its second element, because by then the first element is already in the notebook.

A hash map is that notebook: the value is the key, and the index is what we stored with it.

## 6. Brute-Force Approach

Try every pair of positions.

```
for j from 1 to n - 1:
    for i from 0 to j - 1:
        if nums[i] + nums[j] == target:
            return [i, j]
return []
```

This uses O(1) extra space, and starting `i` below `j` makes sure an element is never paired with itself. But it checks up to n(n - 1)/2 pairs, so it takes O(n²) time. Most of that work is wasted: for each `j`, we already know the only value of `nums[i]` that could work, and we search for it one element at a time.

## 7. Improved Approach

Sort the elements and use two pointers from both ends, as in the [Two Pointers](../../../../Patterns/TwoPointers.md) guide. Sorting the values alone would lose their original positions, so sort an array of indexes by the values they point to instead.

```
order = [0, 1, ..., n - 1] sorted by nums[order[k]]
left = 0, right = n - 1
while left < right:
    sum = nums[order[left]] + nums[order[right]]
    if sum == target: return [order[left], order[right]]
    if sum < target: left = left + 1
    else: right = right - 1
return []
```

This takes O(n log n) time for the sort and O(n) extra space for the index array, and in Java sorting indexes by value needs an `Integer[]` with a comparator, which boxes every index. It is a real improvement over O(n²), and it is worth knowing because it becomes the best choice when the input is already sorted: then the index array is not needed and the search takes O(n) time with O(1) extra space. For unsorted input, the hash map below is faster.

## 8. Preferred Approach

**Core idea:** Scan from left to right. For each element `x`, look up `target - x` in a hash map of the values seen so far. If it is there, return its index and the current index. Otherwise, store `x` with its index.

**Invariant:** At the start of the loop for index `i`, the map holds a value-to-index entry for every value among `nums[0..i - 1]`, and no pair inside `nums[0..i - 1]` adds up to `target`.

**Why it is correct:** The invariant holds before the first step, because the map and the prefix are both empty. At index `i`, any valid pair that ends at `i` needs an earlier element equal to `target - nums[i]`, and the invariant says the map holds every earlier value, so the lookup finds it if it exists. If the lookup fails, no pair ends at `i`, and after storing `nums[i]` the invariant holds for `i + 1`. Since every pair has a second element, the scan finds a pair when it reaches the first index that completes one. The lookup happens before `nums[i]` is stored, so the index found is always smaller than `i`, and an element is never paired with itself.

**Steps:**

1. Create an empty `HashMap<Integer, Integer>` from value to index.
2. For each index `i` from left to right:
   1. Compute `complement = target - nums[i]`.
   2. If the map contains `complement`, return `[map.get(complement), i]`.
   3. Store `nums[i] -> i` in the map.
3. Return an empty array. The problem guarantees this line is never reached, but it keeps the method safe for other inputs.

The order inside the loop matters: look up first, then store. Storing first would let `[3, 5]` with target 6 pair the 3 with itself.

**Trade-offs:** The map uses O(n) extra memory, and each `Integer` key and value is a separate object, so in practice it uses several times the memory of the array itself. The O(1) lookup is an average, not a guarantee; it assumes the hash function spreads the keys well, which holds for `Integer`. If the array is already sorted, two pointers gives the same O(n) time with O(1) extra space and is the better choice. If memory is very tight and the input is unsorted, sorting with two pointers trades time for a simpler data structure, but it still needs the index array.

## 9. Dry Run

**Input:** `nums = [4, 9, 1, 6]`, `target = 7`

| `i` | `nums[i]` | `complement = 7 - nums[i]` | Map before the lookup | Found? | Action |
|---|---|---|---|---|---|
| 0 | 4 | 3 | `{}` | No | Store `4 -> 0` |
| 1 | 9 | -2 | `{4: 0}` | No | Store `9 -> 1` |
| 2 | 1 | 6 | `{4: 0, 9: 1}` | No | Store `1 -> 2` |
| 3 | 6 | 1 | `{4: 0, 9: 1, 1: 2}` | Yes, at index 2 | Return `[2, 3]` |

The method returns `[2, 3]`.

At `i = 2`, the value 1 needs a 6, which has not been seen yet, so 1 is only stored. The pair is found one step later, when 6 asks for a 1 and finds it in the map. This is the pattern for every pair: the first element is stored, and the second element finds it. The complement at `i = 1` is negative, which needs no special handling.

For `nums = [3, 5, 3]` and `target = 6`, the first 3 asks for a 3 while the map is still empty, so it cannot pair with itself. The second 3, at index 2, finds the first one and the method returns `[0, 2]`.

## 10. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
nums = [4, 9, 1, 6], target = 7 -> [2, 3]
nums = [3, 5, 3], target = 6 -> [0, 2]
nums = [5, 7], target = 12 -> [0, 1]
nums = [-3, 8, 2, -5], target = -8 -> [0, 3]
nums = [0, 4, 0], target = 0 -> [0, 2]
nums = [1000000000, -999999999, -1], target = -1000000000 -> [1, 2]
nums = [1, 2, 3], target = 100 -> []
```

The sixth case reaches the extreme complement: for `nums[0] = 10^9` the method looks up `-10^9 - 10^9 = -2 × 10^9`, which still fits in an `int`. The last case has no answer, which the problem rules out, and shows the empty result.

Runs on Java 8, 11, 17, 21 and 25.

## 11. Complexity Analysis

- **Time:** O(n) on average, because each element is visited at most once and does one `get` and at most one `put`, each O(1) on average for `Integer` keys. If many keys collide, a lookup can be slower. Since Java 8, a crowded `HashMap` bucket with `Comparable` keys such as `Integer` becomes a balanced tree, which limits each lookup to O(log n).
- **Auxiliary space:** O(n), because in the worst case the pair is found at the last element, and by then the map holds an entry for every earlier element.

## 12. Edge Cases

- **Only two elements,** such as `[5, 7]` with target 12. The pair is found at index 1, after one stored entry.
- **Equal values that form the pair,** such as `[3, 5, 3]` with target 6. Looking up before storing keeps the first 3 from pairing with itself, and the second 3 finds it.
- **Zeros,** such as `[0, 4, 0]` with target 0. Zero is an ordinary key, and the two zeros pair as in the previous case.
- **Negative values and a negative target,** such as `[-3, 8, 2, -5]` with target -8. The complement can be negative or larger than any value in the array; the map does not care.
- **Values at the limits,** such as `[1000000000, -999999999, -1]` with target `-10^9`. The complement reaches `-2 × 10^9`, which fits in an `int`. With wider limits on the values or the target, compute it as a `long`.
- **No valid pair.** The problem guarantees one exists. If it did not, the loop would finish and the method returns an empty array instead of failing.

## 13. Common Mistakes

- **Storing before looking up.** Putting `nums[i]` into the map first and then searching for `target - nums[i]` pairs an element with itself whenever `nums[i]` is exactly half of `target`.
- **Building the whole map first, then searching in a second pass, without checking the index.** This works only if the second pass checks that the found index is different from `i`. The one-pass version avoids the problem entirely.
- **Sorting the array and returning the sorted positions.** Two pointers on a sorted copy finds the right values, but their indexes in the sorted copy are not the indexes in `nums`. Sort an array of indexes, or use hashing.
- **Using `==` on boxed values.** `indexOfValue.get(a) == indexOfValue.get(b)` compares `Integer` objects and fails for values outside -128 to 127. The solution only compares the result of `get` with `null`, and returns it as an `int`.
- **Unboxing a missing key.** `int j = map.get(complement);` throws `NullPointerException` when the key is absent. Check `containsKey` first, or store the result in an `Integer` and test it for `null`, as the solution does.
- **Ignoring overflow.** With these constraints `target - nums[i]` fits in an `int`, but if the limits were wider, computing it in `int` would wrap around and could find a wrong partner. Use `long` in that case.

## 14. Interview Follow-ups

- **The input array is sorted** ([LeetCode 167](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/)). Use two pointers from both ends. It takes O(n) time and O(1) extra space, and the indexes are already the original ones.
- **Return every pair, not just one.** Keep a map from value to the list of indexes where it appeared, and for each element add one pair for every stored index of its complement. The output itself can be O(n²) pairs, for example when every element is 0 and the target is 0.
- **Count the pairs instead of listing them.** Store a count for each value instead of an index, and add the count of the complement at every step. This is O(n) time even when the number of pairs is O(n²).
- **Three values that add up to a target** ([LeetCode 15](https://leetcode.com/problems/3sum/)). Fix one element and solve Two Sum on the rest. With sorting and two pointers this is O(n²) time, and sorting also makes it easy to skip duplicate triplets.
- **Which earlier index is returned when a value repeats?** The solution overwrites the stored index when a value appears again, so it returns the latest earlier occurrence. To return the earliest one instead, use `putIfAbsent`. With exactly one valid answer, both give the same result.

## 15. Revision Summary

For each element `x`, the only possible partner is `target - x`, so store every value seen so far in a hash map from value to index and look the partner up in O(1) on average. Look up before storing, so an element is never paired with itself, and the pair is found when its second element is reached. This is the complement lookup idea from the [Hashing](../../../03-Hashing/README.md) guide, and it takes O(n) time and O(n) auxiliary space. When the input is already sorted, [Two Pointers](../../../../Patterns/TwoPointers.md) solves it in O(n) time with O(1) extra space.
