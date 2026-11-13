# Longest Substring Without Repeating Characters

**Source:** [LeetCode 3](https://leetcode.com/problems/longest-substring-without-repeating-characters/) · **Difficulty:** Medium · **Topic:** [String](../../README.md) · **Patterns:** [Sliding Window](../../../../Patterns/SlidingWindow.md) (variable size), [Hashing](../../../../Patterns/Hashing.md)

## 1. Problem Statement

You are given a string `s`. Find the longest substring in which every character is different, and return its length.

A substring is a contiguous run of characters. In `"pwwkew"`, `"wke"` is a substring, but `"pwke"` is not, because its characters are not next to each other in `s`. Spaces, digits and symbols are characters like any other, so `"a a"` has a repeat.

**Constraints:**

- `0 <= s.length <= 5 * 10^4`
- `s` contains English letters, digits, symbols and spaces.

## 2. Examples

**Example 1**

```
Input:  s = "abcabcbb"
Output: 3
```

`"abc"` has three different characters. Every substring of length 4 contains a repeat, for example `"abca"`.

**Example 2**

```
Input:  s = "bbbbb"
Output: 1
```

Every substring longer than one character contains two `b`s.

**Example 3**

```
Input:  s = "pwwkew"
Output: 3
```

`"wke"` is the answer. `"pwke"` has four different characters, but it is not contiguous.

## 3. Pattern Recognition

**What is the problem asking?** Among all contiguous ranges of `s` that contain no repeated character, how long is the longest?

**What clues are present?**

- "Substring", so the answer is a contiguous range.
- "Longest", so we want the best range under a condition.
- The condition, "no character appears twice", can be tracked with a set or a table of characters, updated one character at a time.
- The condition is monotonic: if a range has no repeats, every smaller range inside it has none either, and if a range has a repeat, every larger range around it has one too.

**Which patterns should I consider?** Checking every substring, a variable-size [Sliding Window](../../../../Patterns/SlidingWindow.md) whose contents are kept in a `HashSet`, and the same window that remembers the last index of each character so that the left edge can jump forward in one step. All three use the [Hashing](../../../../Patterns/Hashing.md) idea of a "seen" lookup.

**Why does the chosen pattern fit?** The monotonic condition is exactly what the variable window needs. When a new character on the right creates a repeat, no window that starts at the current left edge (or before the earlier copy of that character) can be valid any more, so the left edge can only move forward. Neither edge ever moves back, which keeps the work linear.

**Why are the alternatives less suitable?** Checking every substring repeats almost the same work for every start index and takes O(n²) time or worse. The `HashSet` window is O(n) and easy to get right, but it removes characters one at a time until the repeat is gone. Storing the last index of each character lets the left edge jump straight past the earlier copy, so each character is handled once. All three give the same answer; the difference is the amount of work.

**What assumptions must hold?** The characters are ASCII (codes 0 to 127), as the constraints say, so an `int[128]` table indexed by the character works. For arbitrary Unicode text, use a `HashMap<Integer, Integer>` keyed by code point (see the follow-ups).

**How do the constraints influence the choice?** With up to 5 × 10^4 characters, an O(n²) method does over a billion steps in the worst case, which is too slow. The O(n) window is expected. Since the answer can be at most 128 for ASCII input, the brute force below is actually O(n × 128) when it stops at the first repeat, but that bound depends on the small alphabet and does not hold for general text.

## 4. Prerequisites

- [String](../../README.md): `charAt`, and using a character as an array index, as in `int[128]`.
- [Sliding Window](../../../../Patterns/SlidingWindow.md): the variable-size form for "longest" questions. That guide's Java example already solves this problem with a map of counts; this page explains the problem in depth and adds the jumping version.
- [Hashing](../../../../Patterns/Hashing.md): the seen-set idea, here stored as a table of last positions.

## 5. Intuition

Picture the answer as a window `[left, right]` that slides along the string. Move `right` forward one character at a time. As long as the new character is not already inside the window, the window simply grows.

When the new character `c` is already inside the window, the window is now invalid. Any valid window that ends at `right` must start after the earlier copy of `c`. Starting anywhere earlier would include both copies. So the new left edge is "one past the earlier `c`", and we can move there directly if we remember where each character was last seen.

There is one trap. The table of last positions also remembers characters that have already left the window. In `"abba"`, when `right` reaches the final `a`, the window is `"b"` starting at index 2, and the earlier `a` at index 0 is no longer in it. Jumping to index 1 would move the left edge backwards and bring the second `b` back in. So we only jump when the earlier copy is at or after `left`.

## 6. Brute-Force Approach

For every start index, extend to the right until a character repeats.

```
longest = 0
for start from 0 to n - 1:
    seen = empty set
    for end from start to n - 1:
        if s[end] is in seen: stop this start
        add s[end] to seen
        longest = max(longest, end - start + 1)
return longest
```

Stopping at the first repeat is correct because of the monotonic condition: once `s[start..end]` has a repeat, every longer substring with the same start has it too. For general text this is O(n²) time. With ASCII input the inner loop runs at most 129 times, but the method still does the same work again for every start, which the window avoids. It uses O(m) space for the set, where m is the alphabet size.

## 7. Improved Approach

Keep one window and a `HashSet` of the characters inside it.

```
left = 0, longest = 0, window = empty set
for right from 0 to n - 1:
    while s[right] is in window:
        remove s[left] from window
        left = left + 1
    add s[right] to window
    longest = max(longest, right - left + 1)
return longest
```

When the new character is already inside, the `while` loop removes characters from the left until the earlier copy has gone. Each character is added once and removed at most once, so the total work is O(n) even though there is a loop inside a loop. This is the general "longest" form from the Sliding Window guide, and it is a good answer in an interview. The preferred approach below does the same thing with a direct jump instead of the removal loop.

## 8. Preferred Approach

**Core idea:** Keep `lastSeen[c]`, the index where character `c` was last seen. When `s[right]` was last seen at or after `left`, move `left` to one past that index. Then record `right` as the new last position and update the answer.

**Invariant:** After processing index `right`, the window `s[left..right]` has no repeated character, and it is the longest such substring that ends at `right`. `longest` is the length of the longest valid substring that ends at or before `right`.

**Why it is correct:** Suppose the invariant holds for `right - 1`, and let `c = s[right]` and `p = lastSeen[c]`. If `p < left`, then `c` does not occur in `s[left..right-1]`, so adding it keeps the window valid. No valid window ending at `right` can start before `left`, because it would contain `s[left-1..right-1]`, which the invariant says is not valid. So `[left, right]` is the longest one. If `p >= left`, then `c` occurs at `p` inside the window, and any window ending at `right` that starts at or before `p` contains `c` twice. Starting at `p + 1` removes that copy, and `s[p+1..right-1]` is part of a valid window, so `s[p+1..right]` is valid and is the longest one ending at `right`. Since every substring ends at some index, taking the maximum over all `right` gives the answer.

**Steps:**

1. Create `int[] lastSeen = new int[128]` and fill it with `-1`. Set `left = 0` and `longest = 0`.
2. For each `right` from 0 to `n - 1`:
   1. Let `c = s.charAt(right)`.
   2. If `lastSeen[c] >= left`, set `left = lastSeen[c] + 1`.
   3. Set `lastSeen[c] = right`.
   4. Set `longest = max(longest, right - left + 1)`.
3. Return `longest`.

**Trade-offs:** The table of 128 entries is tied to ASCII. The `HashSet` version works for any `char` without change, and some people find it easier to reason about. Filling the table with `-1` matters: if it were left at 0, a character at index 0 and a character never seen would look the same.

## 9. Dry Run

**Input:** `s = "tmmzuxt"`

Each row shows the state after processing index `right`. "Last seen" is the value of `lastSeen[c]` before it is updated.

| `right` | `c` | Last seen | `left` | Window | Length | `longest` |
|---|---|---|---|---|---|---|
| 0 | `t` | -1 | 0 | `t` | 1 | 1 |
| 1 | `m` | -1 | 0 | `tm` | 2 | 2 |
| 2 | `m` | 1 | 2 | `m` | 1 | 2 |
| 3 | `z` | -1 | 2 | `mz` | 2 | 2 |
| 4 | `u` | -1 | 2 | `mzu` | 3 | 3 |
| 5 | `x` | -1 | 2 | `mzux` | 4 | 4 |
| 6 | `t` | 0 | 2 | `mzuxt` | 5 | 5 |

The method returns 5.

At `right = 2`, the second `m` was last seen at index 1, which is inside the window `[0, 1]`, so `left` jumps to 2. At `right = 6`, the second `t` was last seen at index 0, but the window now starts at 2, so that `t` has already left it. `left` stays at 2, and the window grows to `"mzuxt"`. A version that jumps whenever the character was seen before, without comparing with `left`, would set `left` to 1 here and wrongly report 6, because the window `"mmzuxt"` contains two `m`s. This table was printed from a traced run of the code.

## 10. Java Solution

See [Solution.java](Solution.java). To run it:

```bash
javac Solution.java
java Solution
```

Expected output:

```
"abcabcbb" -> 3
"bbbbb" -> 1
"pwwkew" -> 3
"" -> 0
" " -> 1
"abba" -> 2
"dvdf" -> 3
"tmmzuxt" -> 5
"a b!c a" -> 5
```

`"abba"` and `"tmmzuxt"` both contain a character whose earlier copy has already left the window. `"dvdf"` shows that the left edge does not always move to the current character: the answer `"vdf"` starts one past the first `d`. `"a b!c a"` shows that spaces and symbols count as characters: the answer is `"b!c a"`.

Runs on Java 8, 11, 17, 21 and 25.

## 11. Complexity Analysis

- **Time:** O(n), where n is the length of `s`. The loop visits each index once and does constant work for it: one table lookup, at most one assignment to `left` and one comparison. `left` never moves backwards.
- **Auxiliary space:** O(1). The table always has 128 entries, whatever the length of `s`. In general terms it is O(m) for an alphabet of size m. The `HashSet` version also uses O(m), since the window never holds more than m distinct characters.

## 12. Edge Cases

- **Empty string.** The loop never runs and the answer is 0.
- **A single character,** such as `" "`. The answer is 1. A space is a normal character.
- **All characters the same,** such as `"bbbbb"`. Every step jumps `left` to `right`, and the answer is 1.
- **All characters different.** `left` stays at 0 and the answer is `n`. For ASCII input, `n` can be at most 128 in this case.
- **An earlier copy that has already left the window,** such as `"abba"` and `"tmmzuxt"`. The `lastSeen[c] >= left` check keeps `left` from moving backwards.
- **The best window in the middle,** such as `"dvdf"`, where the answer starts at index 1, not 0.

## 13. Common Mistakes

- **Jumping whenever a character was seen before.** Writing `if (lastSeen[c] != -1) left = lastSeen[c] + 1;` can move `left` backwards. For `"abba"` it returns 3 and for `"tmmzuxt"` it returns 6. Compare with `left`, or write `left = Math.max(left, lastSeen[c] + 1)`.
- **Leaving the table filled with 0.** Then a character that was never seen looks as if it had been seen at index 0, so while `left` is 0 it causes a false jump. For `"ab"` this returns 1 instead of 2.
- **Updating `longest` before moving `left`.** The length must be measured after the window is valid again.
- **Moving `left` to `right` on a repeat.** For `"dvdf"`, that would discard `v` and return 2 instead of 3. The window should start one past the earlier copy, not at the current character.
- **Using `int[26]` because the examples are lowercase.** The constraints allow digits, symbols and spaces, so `s.charAt(i) - 'a'` would give a negative or too-large index.
- **Confusing substring with subsequence.** The number of distinct characters in `s` is not the answer, because the characters must be contiguous.

## 14. Interview Follow-ups

- **Return the substring itself, not its length.** Also store the `left` and `right` of the best window when `longest` improves, and return `s.substring(bestLeft, bestRight + 1)`.
- **The text can contain any Unicode character.** A `char` is one UTF-16 unit, and characters outside the Basic Multilingual Plane, such as many emoji, take two. Walk the string by code point (`s.codePointAt(i)` and `Character.charCount`) and keep a `HashMap<Integer, Integer>` of last positions. The window logic does not change.
- **At most `k` distinct characters** ([LeetCode 340](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/), a premium problem). Keep a map of counts instead of last positions, and shrink from the left while the map has more than `k` keys. This problem is the case where every character may appear at most once.
- **Longest Repeating Character Replacement** ([LeetCode 424](https://leetcode.com/problems/longest-repeating-character-replacement/)). The window is valid when its length minus the count of its most common character is at most `k`. It is the same variable window with a different condition.
- **Minimum Window Substring** ([LeetCode 76](https://leetcode.com/problems/minimum-window-substring/)). A "shortest" window that must contain every character of another string, using counts and the shrink-while-valid form.

## 15. Revision Summary

"Longest substring" with a condition that stays true for smaller ranges points to a variable-size [Sliding Window](../../../../Patterns/SlidingWindow.md). Grow the window on the right, and when the new character already appears inside it, move the left edge to one past its earlier copy, which the table `lastSeen` gives in one step. Only jump when that copy is at or after `left`, or the window moves backwards. It takes O(n) time and O(1) auxiliary space for ASCII (O(m) for an alphabet of m characters); a `HashSet` window that removes characters one at a time is also O(n) and a good first answer.
