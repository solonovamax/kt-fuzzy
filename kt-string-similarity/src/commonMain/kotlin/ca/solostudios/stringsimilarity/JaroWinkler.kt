/*
 * kt-fuzzy - A Kotlin library for fuzzy string matching
 * Copyright (c) 2015-2023 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file JaroWinkler.kt is part of kotlin-fuzzy
 * Last modified on 18-07-2023 07:22 p.m.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * KT-FUZZY IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.solostudios.stringsimilarity

import ca.solostudios.stringsimilarity.interfaces.NormalizedStringDistance
import ca.solostudios.stringsimilarity.interfaces.NormalizedStringSimilarity
import ca.solostudios.stringsimilarity.util.minMaxByLength
import kotlin.math.max
import kotlin.math.min

/**
 * The Jaro–Winkler distance metric is designed and best suited for short
 * strings such as person names, and to detect typos; it is (roughly) a
 * variation of Damerau-Levenshtein, where the substitution of 2 close
 * characters is considered less important then the substitution of 2 characters
 * that a far from each other.
 * Jaro-Winkler was developed in the area of record linkage (duplicate
 * detection) (Winkler, 1990). It returns a value in the interval [0.0, 1.0].
 * The distance is computed as 1 - Jaro-Winkler similarity.
 * @param threshold Returns the current value of the threshold used for adding the Winkler
 * bonus. The default value is 0.7.
 * @author Thibault Debatty
 */
public class JaroWinkler(
    public val threshold: Double = DEFAULT_THRESHOLD,
) : NormalizedStringSimilarity,
    NormalizedStringDistance {
    /**
     * Compute Jaro-Winkler similarity.
     *
     * @param s1 The first string to compare.
     * @param s2 The second string to compare.
     * @return The Jaro-Winkler similarity in the range [0, 1]
     */
    override fun similarity(s1: String, s2: String): Double {
        if (s1 == s2)
            return 1.0

        val mtp = matches(s1, s2)
        val m = mtp.matches.toDouble()

        if (mtp.matches == 0)
            return 0.0

        val jaroSimilarity = ((m / s1.length) + (m / s2.length) + (m - mtp.transpositions) / m) / 3

        return if (jaroSimilarity > threshold)
            jaroSimilarity + min(JW_COEFFICIENT, 1.0 / mtp.longestLength) * mtp.commonPrefixLength * (1 - jaroSimilarity)
        else
            jaroSimilarity
    }

    /**
     * Return 1 - similarity.
     *
     * @param s1 The first string to compare.
     * @param s2 The second string to compare.
     * @return 1 - similarity.
     */
    override fun distance(s1: String, s2: String): Double {
        return 1.0 - similarity(s1, s2)
    }

    private fun matches(s1: String, s2: String): Matches {
        val (shortest, longest) = minMaxByLength(s1, s2)
        val searchRange = max(longest.length / 2 - 1, 0)
        val matchIndexes = IntArray(shortest.length) { -1 }

        val matchFlags = BooleanArray(longest.length)

        val matches = shortest.mapIndexedNotNull { index, char ->
            val low = max(index - searchRange, 0)
            val high = min(index + searchRange + 1, longest.length)
            val matchIndex = (low until high).firstOrNull { i ->
                !matchFlags[i] && char == longest[i]
            }
            if (matchIndex != null) {
                matchIndexes[index] = matchIndex
                matchFlags[matchIndex] = true
                char
            } else {
                null
            }
        }.size

        val ms1 = shortest.filterIndexed { i, _ -> matchIndexes[i] != -1 }.toCharArray()
        val ms2 = longest.filterIndexed { i, _ -> matchFlags[i] }.toCharArray()

        // val transpositions = ms1.zip(ms2).count { (c1, c2) -> c1 != c2 } / 2
        var transpositions = 0
        ms1.forEachIndexed { i, c1 ->
            if (c1 != ms2[i])
                transpositions++
        }

        val commonPrefixLength = shortest.commonPrefixWith(longest).length

        return Matches(matches, transpositions / 2, commonPrefixLength, longest.length)
    }

    private data class Matches(
        val matches: Int,
        val transpositions: Int,
        val commonPrefixLength: Int,
        val longestLength: Int,
    )

    private companion object {
        private const val DEFAULT_THRESHOLD = 0.7
        private const val JW_COEFFICIENT = 0.1
    }
}
