/*
 * kt-fuzzy - A Kotlin library for fuzzy string matching
 * Copyright (c) 2023 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file Constants.kt is part of kotlin-fuzzy
 * Last modified on 19-10-2023 05:35 p.m.
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

package ca.solostudios.stringsimilarity.util

/**
 * Various misc. constants
 */
public object Constants {
    /**
     * The default weight to be used.
     */
    public const val DEFAULT_WEIGHT: Double = 1.0

    /**
     * The maximum weight that should reasonably be used.
     * If there is no maximum, then it can sometimes return
     * [Double.POSITIVE_INFINITY], which is very unwanted.
     */
    public const val MAX_REASONABLE_WEIGHT: Double = 1E10

    /**
     * The minimum weight that should reasonably be used.
     *
     * Negative weights make no sense.
     */
    public const val MIN_REASONABLE_WEIGHT: Double = 0.0
}
