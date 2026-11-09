public class Solution {

    /**
     * Returns true if t is an anagram of s: both strings use exactly the same
     * letters, each the same number of times. Both strings must contain only
     * lowercase English letters 'a' to 'z'.
     */
    public static boolean isAnagram(String s, String t) {
        // Strings of different lengths can never use the same letters equally often.
        if (s.length() != t.length()) {
            return false;
        }

        // counts[0] is for 'a', counts[1] for 'b', and so on up to counts[25] for 'z'.
        int[] counts = new int[26];

        // Add one for every letter of s and subtract one for every letter of t.
        // Both strings have the same length, so one loop handles both.
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }

        // Every letter must appear equally often in both strings, so every
        // count must be back at zero.
        for (int count : counts) {
            if (count != 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String[][] testCases = {
            {"listen", "silent"},
            {"rat", "car"},
            {"aab", "abb"},
            {"a", "a"},
            {"ab", "abc"},
            {"anagram", "nagaram"},
            {"zzzz", "zzzz"}
        };

        for (String[] testCase : testCases) {
            System.out.println("s = \"" + testCase[0] + "\", t = \"" + testCase[1]
                    + "\" -> " + isAnagram(testCase[0], testCase[1]));
        }
    }
}
