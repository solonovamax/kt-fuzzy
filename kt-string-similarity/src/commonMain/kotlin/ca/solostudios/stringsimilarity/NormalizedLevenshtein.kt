/*
 * kt-fuzzy - A Kotlin library for fuzzy string matching
 * Copyright (c) 2015-2023 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NormalizedLevenshtein.kt is part of kotlin-fuzzy
 * Last modified on 18-07-2023 09:40 p.m.
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

import ca.solostudios.stringsimilarity.interfaces.MetricStringDistance
import ca.solostudios.stringsimilarity.interfaces.NormalizedStringDistance
import ca.solostudios.stringsimilarity.interfaces.NormalizedStringSimilarity
import ca.solostudios.stringsimilarity.util.maxLength

/**
 * This distance is computed as Levenshtein distance divided by the length of
 * the longest string. The resulting value is always in the range \(&#91;0, 1]\),
 * but it is not a [metric][MetricStringDistance] anymore! The similarity is computed as 1 - normalized
 * distance.
 *
 * The normalized Levenshtein distance between Strings \(X\) and \(Y\) is:
 * \(\frac{\lvert Levenshtein(X, Y) \rvert}{max(\lvert X \rvert, \vert Y \rvert)}\).
 *
 * @param limit The maximum result to compute before stopping, before normalization.
 * @author Thibault Debatty, solonovamax
 * @see Levenshtein
 */
public class NormalizedLevenshtein(
    limit: Int = Int.MAX_VALUE,
) : NormalizedStringDistance,
    NormalizedStringSimilarity {
    private val levenshtein: Levenshtein = Levenshtein(limit)

    /**
     * Computes the normalized Levenshtein distance of two strings.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return The normalized Levenshtein distance.
     * @see NormalizedStringDistance
     */
    override fun distance(s1: String, s2: String): Double {
        if (s1 == s2)
            return 0.0
        if (s1.isEmpty() || s2.isEmpty())
            return 1.0

        return levenshtein.distance(s1, s2) / maxLength(s1, s2)
    }

    /**
     * Computes the normalized Levenshtein similarity of two strings.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return The normalized Levenshtein similarity.
     * @see NormalizedStringSimilarity
     */
    override fun similarity(s1: String, s2: String): Double {
        return 1.0 - distance(s1, s2)
    }
}
