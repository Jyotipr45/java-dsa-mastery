# Hashing

## What

The Hashing pattern replaces a search with a lookup. Instead of scanning the input again to answer a question about a value, you store what you have already seen in a hash set or hash map, and ask the question in O(1) time on average. A nested loop that compares every element with every other element often becomes a single loop.

Most hashing solutions use one of four forms:

- **Seen set.** A `HashSet` of the values visited so far answers "have I met this value before?". It finds duplicates and the first repeated element.
- **Frequency count.** A `HashMap` from value to count answers "how many times does this appear?". Two collections are compared by comparing their counts.
- **Complement lookup.** For each element, work out the one value that would complete it, and look that value up among the elements already seen. Two Sum is the standard example.
- **Grouping by key.** Compute a key that is equal for items that belong together, such as the sorted letters of a word, and use it to collect items into lists.

How a `HashMap` stores keys, why its operations are O(1) on average, and the usual Java pitfalls are explained in the [Hashing topic guide](../DSA/03-Hashing/README.md). This guide is about recognizing when to use one.

## When

Consider Hashing when:

- The brute force has an inner loop that searches for a particular value, and that value is known before the search starts.
- The question is about values and how often they occur, not about their order or position.
- The input is not sorted, and sorting it would either cost too much or destroy information you need, such as the original indexes.
- You need to compare two collections while ignoring order, as in anagrams or "same multiset of values".
- Items must be put into groups that share some property you can compute from each item alone.

## Why it works

All four forms rely on the same property: each question can be answered by looking up one exact key. A hash table finds an exact key without looking at the other keys, so the work per question does not grow with the amount of data stored.

The forms differ only in what the key is:

- In a seen set, the key is the value itself.
- In a frequency count, the key is the value and the stored number is how often it has appeared.
- In a complement lookup, the key is the partner the current element needs. For a pair sum, if the current value is `x`, the only possible partner is `target - x`. Because the partner is fully determined, one lookup replaces a loop over every candidate partner.
- In grouping, the key is a canonical form. Two items belong to the same group exactly when their canonical forms are equal, so equal keys collect exactly one group.

Correctness also depends on order. When you look up before recording the current element, the table holds only earlier elements, so an element is never matched with itself and each pair is counted once, when its second element is reached.

## How

**Seen set**

1. Create an empty set.
2. For each element, if the set already contains it, you have found a repeat. Otherwise add it.

**Frequency count**

1. For each element of the first collection, increase its count.
2. For each element of the second collection, decrease its count. If a count would drop below zero, the second collection has an extra copy of that element.

**Complement lookup**

1. Create an empty map from value to whatever the answer needs: an index, a count, or a list of indexes.
2. For each element, compute the partner it needs and look it up in the map.
3. Only then record the current element in the map.

**Grouping by key**

1. For each item, compute its key.
2. Append the item to the list stored under that key, creating the list the first time the key appears.

**Pseudocode**

```
containsDuplicate(nums):
    seen = empty set
    for value in nums:
        if value in seen:
            return true
        add value to seen
    return false

countPairsWithSum(nums, target):
    seen = empty map from value to count
    pairs = 0
    for value in nums:
        pairs = pairs + seen.get(target - value, 0)
        seen[value] = seen.get(value, 0) + 1
    return pairs

groupBy(items, keyOf):
    groups = empty map from key to list
    for item in items:
        append item to groups[keyOf(item)], creating the list if needed
    return the lists in groups
```

**Worked example**

Count the pairs `i < j` in `[3, 1, 3, 5, 3]` whose values add up to 6.

| Index | Value | Needed partner | Times seen | pairs | Map after this step |
|---|---|---|---|---|---|
| start | | | | 0 | {} |
| 0 | 3 | 3 | 0 | 0 | {3: 1} |
| 1 | 1 | 5 | 0 | 0 | {3: 1, 1: 1} |
| 2 | 3 | 3 | 1 | 1 | {3: 2, 1: 1} |
| 3 | 5 | 1 | 1 | 2 | {3: 2, 1: 1, 5: 1} |
| 4 | 3 | 3 | 2 | 4 | {3: 3, 1: 1, 5: 1} |

The answer is 4: the three pairs of 3s, at indexes (0, 2), (0, 4) and (2, 4), and the pair 1 + 5 at indexes (1, 3). At index 0 the map is still empty, so the first 3 cannot pair with itself even though 3 is its own partner. At index 4 the map says two 3s came earlier, so this one element completes two pairs at once. A count is stored instead of an index because the question asks how many pairs exist, not where one of them is.

**Java example**

```java
import java.util.*;

public class HashingPatternDemo {

    // Seen set: returns true as soon as some value appears a second time.
    static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int value : nums) {
            // add returns false when the value is already in the set
            if (!seen.add(value)) {
                return true;
            }
        }
        return false;
    }

    // Frequency count: two strings are anagrams when every character
    // appears the same number of times in both.
    static boolean isAnagram(String first, String second) {
        if (first.length() != second.length()) {
            return false;
        }
        Map<Character, Integer> counts = new HashMap<>();
        for (char c : first.toCharArray()) {
            counts.merge(c, 1, Integer::sum);
        }
        for (char c : second.toCharArray()) {
            int left = counts.getOrDefault(c, 0) - 1;
            if (left < 0) {
                return false; // second uses c more often than first
            }
            counts.put(c, left);
        }
        // Equal lengths and no count below zero means every count is zero.
        return true;
    }

    // Complement lookup: counts the pairs i < j with nums[i] + nums[j] == target.
    static long countPairsWithSum(int[] nums, int target) {
        // Long keys, because target - value can fall outside the int range.
        Map<Long, Integer> seen = new HashMap<>();
        long pairs = 0;
        for (int value : nums) {
            long needed = (long) target - value;
            // Look up before recording, so an element never pairs with itself.
            pairs += seen.getOrDefault(needed, 0);
            seen.merge((long) value, 1, Integer::sum);
        }
        return pairs;
    }

    // Grouping: words with the same sorted letters share a key.
    static Collection<List<String>> groupAnagrams(String[] words) {
        // LinkedHashMap keeps groups in the order their first word appeared,
        // so the printed result is the same on every JVM.
        Map<String, List<String>> groups = new LinkedHashMap<>();
        for (String word : words) {
            char[] letters = word.toCharArray();
            Arrays.sort(letters);
            String key = new String(letters);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }
        return groups.values();
    }

    public static void main(String[] args) {
        System.out.println("duplicate in [4, 1, 7, 1]: " + containsDuplicate(new int[] {4, 1, 7, 1}));
        System.out.println("duplicate in [4, 1, 7]: " + containsDuplicate(new int[] {4, 1, 7}));

        System.out.println("anagram listen/silent: " + isAnagram("listen", "silent"));
        System.out.println("anagram rat/car: " + isAnagram("rat", "car"));
        System.out.println("anagram aab/abb: " + isAnagram("aab", "abb"));

        System.out.println("pairs with sum 6 in [3, 1, 3, 5, 3]: " + countPairsWithSum(new int[] {3, 1, 3, 5, 3}, 6));
        System.out.println("pairs with sum 0 in [0, 0, 0, 0]: " + countPairsWithSum(new int[] {0, 0, 0, 0}, 0));
        System.out.println("pairs with sum 2000000000 in [-1000000000, -1294967296]: "
                + countPairsWithSum(new int[] {-1000000000, -1294967296}, 2000000000));

        String[] words = {"eat", "tea", "tan", "ate", "nat", "bat"};
        System.out.println("anagram groups: " + groupAnagrams(words));
    }
}
```

Output:

```
duplicate in [4, 1, 7, 1]: true
duplicate in [4, 1, 7]: false
anagram listen/silent: true
anagram rat/car: false
anagram aab/abb: false
pairs with sum 6 in [3, 1, 3, 5, 3]: 4
pairs with sum 0 in [0, 0, 0, 0]: 6
pairs with sum 2000000000 in [-1000000000, -1294967296]: 0
anagram groups: [[eat, tea, ate], [tan, nat], [bat]]
```

The second pair-counting line shows why the count is stored: four zeros form six pairs, and adding the stored count at each step finds them all without listing them. The third line checks overflow. `target - value` is `2000000000 + 1000000000` for the second element, which does not fit in an `int`. With `int` arithmetic it wraps around to `-1000000000`, which is the first element, so the method would wrongly report one pair. Computing the partner as a `long` avoids this.

`groupAnagrams` uses a `LinkedHashMap` only so that the groups print in a fixed order. A plain `HashMap` gives the same groups in an order that is not defined.

## Problem clues

- "Contains duplicate", "first repeated", "first unique": a seen set, or a frequency count when the number of occurrences matters.
- "Anagram", "permutation of", "same characters", "ransom note": compare frequency counts.
- "Two values that add up to", "difference equal to `k`", "pair with a given XOR": complement lookup, because the partner is determined by the current value.
- "Number of pairs" rather than "find a pair": store counts, not indexes.
- "Group", "classify", "bucket items that share": grouping by a computed key.
- "Number of subarrays with sum `k`" or "longest subarray with sum `k`": a complement lookup on prefix sums. See [Prefix Sum](PrefixSum.md).
- A brute force whose inner loop asks "is there an element equal to this?".

Clues are hints, not rules. Always check that the pattern's assumptions actually hold for the problem. If the input is sorted, [Two Pointers](TwoPointers.md) often answers the same pair question with no extra memory. If the keys are small integers in a known range, such as lowercase letters, an `int[]` of counts does the same job faster.

## Complexity

- **Time:** O(n) on average for one pass that does a constant number of lookups and insertions per element, assuming a hash function that spreads keys evenly, as the standard Java classes do. A single operation can be slower when many keys share a bucket or the table resizes, but the average stays O(1).
- **Auxiliary space:** O(n) in the worst case, when every element is stored under a different key. A frequency count over a fixed alphabet needs only O(alphabet size).

Hashing a key is not always O(1). A `String` key costs O(length) to hash the first time, and a computed key such as the sorted letters of a word costs whatever it takes to build. Grouping `n` words of length at most `m` by sorted letters therefore takes O(n · m log m) time. Building the key from a count of each letter instead brings that down to O(n · m) for a fixed alphabet.

## Limitations

- It only answers exact-match questions. "Is there a value close to `x`?", "what is the next larger value?" and "how many values lie between `a` and `b`?" need order, so use a `TreeMap`, a `TreeSet` or a sorted array with binary search.
- The O(n) extra memory is real, and boxed keys such as `Integer` make it several times larger than an array of the same values.
- Average O(1) is not a guarantee for every operation. When a strict worst-case bound matters, a balanced tree gives O(log n) every time.
- The key must capture exactly what makes two items equal. A key that is too loose merges groups that should be separate, and a mutable key or an array used as a key breaks lookups, as the [Hashing topic guide](../DSA/03-Hashing/README.md) explains.
- Iteration order of a `HashMap` is not defined. If the answer depends on order, track it separately or use a `LinkedHashMap`.

## Alternatives

| Technique | Use it when | Trade-off |
|---|---|---|
| Brute force over all pairs | The input is tiny | O(n²) time, O(1) extra space, and easy to get right |
| Sorting first | You can afford O(n log n) and do not need the original order | Duplicates become neighbours and pairs can use two pointers, often with O(1) extra space beyond the sort |
| [Two Pointers](TwoPointers.md) | The input is already sorted | O(n) time and O(1) extra space, but needs sorted data |
| Counting array (`int[]`) | Keys are small integers in a known range | Faster and lighter than a map, but wasteful for a large or sparse range |
| `TreeMap` or `TreeSet` | You need order, nearest values or ranges as well as lookups | O(log n) per operation instead of O(1) on average |
| [Prefix Sum](PrefixSum.md) with a map | The question is about subarray sums | Not an alternative so much as a combination: the map stores earlier prefix sums |

The [Array](../DSA/01-Array/README.md), [String](../DSA/02-String/README.md) and [Hashing](../DSA/03-Hashing/README.md) topic guides cover the data structures these techniques rely on.

## Practice problems

- [Two Sum](../DSA/01-Array/Easy/004-two-sum/README.md) (Easy, Array): complement lookup with a map from value to index, looking up before recording so an element never pairs with itself.
- [Valid Anagram](../DSA/02-String/Easy/001-valid-anagram/README.md) (Easy, String): frequency count of two strings, with an `int[26]` array in place of a map because the keys are only the 26 lowercase letters.

## Quick revision

Hashing turns "search the data for this value" into an O(1) average lookup, so many O(n²) pair and duplicate checks become one O(n) pass with O(n) extra space. Use a seen set for repeats, a frequency count to compare collections, a complement lookup when each element determines the one partner it needs, and a computed key to group items. Look up before recording so an element is never matched with itself. It only answers exact-match questions, and when the input is sorted or the keys are small integers, two pointers or a counting array may be simpler and use less memory.
