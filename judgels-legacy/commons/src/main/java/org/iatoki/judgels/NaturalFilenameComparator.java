package org.iatoki.judgels;

import java.util.Comparator;

public final class NaturalFilenameComparator implements Comparator<String> {

    /**
     * Enumeration type denoting the type of a character in a file name
     */
    enum TokenType {
        ALPH,
        NUM,
        OTHER
    }

    /** The maximum value of 1-byte ASCII character. */
    private static final int CHAR_SIZE = 256;

    /** type[c] = the type of character c. */
    private TokenType[] type;

    /** ord[c] = the (modified) ordinal value of character c. */
    private int[] ord;

    /**
     * Constructs a new NaturalFilenameComparator object.
     */
    public NaturalFilenameComparator() {
        // Construct a new ASCII table.
        ord = new int[CHAR_SIZE];

        // Initialize the default ordinal value for all characters.
        for (int i = 0; i < CHAR_SIZE; i++) {
            ord[i] = i;
        }

        // We want to modify part of the ASCII table from
        //      ABC..XYZ$$$abc..xyz
        // into
        //      aAbBcC..xXyYzZ$$$

        // First, shift the ordinal value for all characters between 'Z' and 'a', exclusive, by 26.
        for (char c = 'Z' + 1; c < 'a'; c++) {
            ord[c] += 26;
        }

        // Then, renumber the ordinal value for charaters 'A'..'Z', 'a'..'z'.
        int pos = 'A';
        for (int i = 0; i < 26; i++) {
            ord['a' + i] = pos++;
            ord['A' + i] = pos++;
        }

        // Set up token types.
        type = new TokenType[CHAR_SIZE];

        // Initialize all types as OTHER.
        for (int i = 0; i < CHAR_SIZE; i++) {
            type[i] = TokenType.OTHER;
        }

        // Declare tokens 'A'..'Z', 'a'..'z' as ALPH.
        for (char c = 'A'; c <= 'Z'; c++) {
            type[c] = TokenType.ALPH;
        }
        for (char c = 'a'; c <= 'z'; c++) {
            type[c] = TokenType.ALPH;
        }

        // Declare tokens '0'..'9' as ALPH.
        for (char c = '0'; c <= '9'; c++) {
            type[c] = TokenType.NUM;
        }
    }

    /**
     * Compares two file names naturally.
     *
     * Natural comparison is performed as follows. The strings will be compared chunk by chunk,
     * from left to right. A chunk is defined as maximal contiguous characters of the same type.
     *
     * There are 3 types:
     * - TokenType.ALPH : For characters 'a'..'z', 'A'..'Z'.
     * - TokenType.NUM : For numbers; i.e. '0'..'9'.
     * - TokenType.OTHER : For all other characters.
     *
     * In this way, if a chunk of alphabet characters is surrounded by other symbol characters,
     * they will not affect the ordering. For example, "[A]" will be compared lower to "[AA]"
     * although the ordinal value of ']' is higher than that of 'A'.
     *
     * If two chunks of type number are compared, they will be compared by their true integral
     * values. Leading zeroes will be ignored in the comparison.
     *
     * Additionally, the order of alphabet characters is changed from
     *  A B C ... X Y Z a b c ... x y z
     * into
     *  a A b B c C ... x X y Y z Z
     *
     * @param s1 The first file name
     * @param s2 The second file name
     * @return Negative value if s1 is lower than s2, positive value if s2 is lower than s1,
     *         or zero if they are equal.
     */

    @Override
    public int compare(String s1, String s2) {
        // Check whether either string is null.
        // null strings will be compared lower than non-null strings.

        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 == null) {
            return -1;
        }
        if (s2 == null) {
            return 1;
        }

        // Start comparing the strings.

        // Store the lengths of the strings for convenience.
        int len1 = s1.length(), len2 = s2.length();

        // The current positions in both strings.
        int pos1 = 0, pos2 = 0;

        // Iterate over the strings.
        while (pos1 < len1 && pos2 < len2) {
            // Start building chunks for both strings in the current positions.

            // Store the current chunk types.
            TokenType type1 = type[s1.charAt(pos1)];
            TokenType type2 = type[s2.charAt(pos2)];

            // If both chunks are of type number, compare by their integral values.
            if (type1 == TokenType.NUM && type2 == TokenType.NUM) {
                // Find the most significant digits for both chunks.
                int start1 = pos1;
                while (start1 < len1 && s1.charAt(start1) == '0') {
                    start1++;
                }
                int start2 = pos2;
                while (start2 < len2 && s2.charAt(start2) == '0') {
                    start2++;
                }

                // Corner case: if either chunk is literal zero, there will be no non-zero
                // most significant digit. So, set the last 0 as the most significant digit.
                if (start1 == len1 || type[s1.charAt(start1)] != TokenType.NUM) {
                    start1--;
                }
                if (start2 == len2 || type[s2.charAt(start2)] != TokenType.NUM) {
                    start2--;
                }

                // Find the least significant digits for both chunks.
                int end1 = start1;
                while (end1 + 1 < len1 && type[s1.charAt(end1 + 1)] == TokenType.NUM) {
                    end1++;
                }
                int end2 = start2;
                while (end2 + 1 < len2 && type[s2.charAt(end2 + 1)] == TokenType.NUM) {
                    end2++;
                }

                // If the lengths of the chunks are different, the ordering can be
                // immediately determined. Shorter number will be lower.
                if (end1 - start1 < end2 - start2) {
                    return -1;
                }
                if (end1 - start1 > end2 - start2) {
                    return 1;
                }

                // If they are of equal length, compare the digits, from the most to
                // the least significant digits.

                // The complexity will still be linear as each digit is encountered only twice:
                // in finding for the least significant digit and in this loop.
                for (int i = 0; i < end1 - start1 + 1; i++) {
                    if (s1.charAt(start1 + i) < s2.charAt(start2 + i)) {
                        return -1;
                    }
                    if (s1.charAt(start1 + i) > s2.charAt(start2 + i)) {
                        return 1;
                    }
                }

                // Advance the current positions.
                pos1 = end1 + 1;
                pos2 = end2 + 1;
            } else {
                // Iterate the current chunks.
                while ((pos1 < len1 && type[s1.charAt(pos1)] == type1) && (pos2 < len2 && type[s2.charAt(pos2)] == type2)) {
                    // If the characters at the respective positions are different, the ordering can be
                    // immediately determined.
                    if (ord[s1.charAt(pos1)] < ord[s2.charAt(pos2)]) {
                        return -1;
                    }
                    if (ord[s1.charAt(pos1)] > ord[s2.charAt(pos2)]) {
                        return 1;
                    }

                    pos1++;
                    pos2++;
                }

                // At this point, the first min(length of chunk 1, length of chunk2) characters are equal.

                // If chunk 2 is longer than chunk 1, then chunk 1 < chunk 2.
                if (pos2 < len2 && type[s2.charAt(pos2)] == type2) {
                    return -1;
                }

                // If chunk 1 is longer than chunk 2, then chunk 2 < chunk 1.
                if (pos1 < len1 && type[s1.charAt(pos1)] == type1) {
                    return 1;
                }
            }
        }

        // At this point, the first min(number of chunks in s1, number of chunks in s2) chunks are equal.

        // If s2 has more chunks, then chunk 1 < chunk 2.
        if (pos2 < len2) {
            return -1;
        }

        // If s1 has more chunks, then chunk 1 > chunk 2.
        if (pos1 < len1) {
            return 1;
        }

        // At this point, all chunks are naturally equal.
        // Resort to the default comparison function.
        return s1.compareTo(s2);
    }
}
