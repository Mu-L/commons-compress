/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.compressors.lz77support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ParametersTest {

    private static Parameters newParameters(final int windowSize) {
        return Parameters.builder(windowSize).build();
    }

    private static Parameters newParameters(final int windowSize, final int minBackReferenceLength, final int maxBackReferenceLength, final int maxOffset,
            final int maxLiteralLength) {
        return Parameters.builder(windowSize).withMinBackReferenceLength(minBackReferenceLength).withMaxBackReferenceLength(maxBackReferenceLength)
                .withMaxOffset(maxOffset).withMaxLiteralLength(maxLiteralLength).build();
    }

    @Test
    void testAllParametersUsuallyTakeTheirSpecifiedValues() {
        final Parameters p = newParameters(256, 4, 5, 6, 7);
        assertEquals(256, p.getWindowSize());
        assertEquals(4, p.getMinBackReferenceLength());
        assertEquals(5, p.getMaxBackReferenceLength());
        assertEquals(6, p.getMaxOffset());
        assertEquals(7, p.getMaxLiteralLength());
    }

    @Test
    void testDefaultConstructor() {
        final Parameters p = newParameters(128);
        assertEquals(128, p.getWindowSize());
        assertEquals(3, p.getMinBackReferenceLength());
        assertEquals(127, p.getMaxBackReferenceLength());
        assertEquals(127, p.getMaxOffset());
        assertEquals(128, p.getMaxLiteralLength());
    }

    @Test
    void testMaxBackReferenceLengthIsMinBackReferenceLengthIfBothAreEqual() {
        final Parameters p = newParameters(128, 2, 3, 4, 5);
        assertEquals(3, p.getMaxBackReferenceLength());
    }

    @Test
    void testMaxBackReferenceLengthIsMinBackReferenceLengthWhenSmallerThanMinBackReferenceLength() {
        final Parameters p = newParameters(128, 2, 2, 4, 5);
        assertEquals(3, p.getMaxBackReferenceLength());
    }

    @Test
    void testMaxBackReferenceLengthIsMinBackReferenceLengthWhenSmallerThanMinBackReferenceLengthReversedInvocationOrder() {
        final Parameters p = Parameters.builder(128).withMaxBackReferenceLength(2).withMinBackReferenceLength(2).withMaxOffset(4).withMaxLiteralLength(5)
                .build();
        assertEquals(3, p.getMaxBackReferenceLength());
    }

    @Test
    void testMaxLiteralLengthIsWindowSizeIfSetTo0() {
        final Parameters p = newParameters(128, 2, 3, 4, 0);
        assertEquals(128, p.getMaxLiteralLength());
    }

    @Test
    void testMaxLiteralLengthIsWindowSizeIfSetToANegativeValue() {
        final Parameters p = newParameters(128, 2, 3, 0, -1);
        assertEquals(128, p.getMaxLiteralLength());
    }

    @Test
    void testMaxLiteralLengthIsWindowSizeIfSetToAValueTooBigToHoldInSlidingWindow() {
        final Parameters p = newParameters(128, 2, 3, 0, 259);
        assertEquals(128, p.getMaxLiteralLength());
    }

    @Test
    void testMaxOffsetIsWindowSizeMinus1IfBiggerThanWindowSize() {
        final Parameters p = newParameters(128, 2, 3, 129, 5);
        assertEquals(127, p.getMaxOffset());
    }

    @Test
    void testMaxOffsetIsWindowSizeMinus1IfSetTo0() {
        final Parameters p = newParameters(128, 2, 3, 0, 5);
        assertEquals(127, p.getMaxOffset());
    }

    @Test
    void testMaxOffsetIsWindowSizeMinus1IfSetToANegativeValue() {
        final Parameters p = newParameters(128, 2, 3, -1, 5);
        assertEquals(127, p.getMaxOffset());
    }

    @Test
    void testMinBackReferenceLengthIsAtLeastThree() {
        final Parameters p = newParameters(128, 2, 3, 4, 5);
        assertEquals(3, p.getMinBackReferenceLength());
    }

    @Test
    void testWindowSizeMustBeAPowerOfTwo() {
        assertThrows(IllegalArgumentException.class, () -> newParameters(100, 200, 300, 400, 500));
    }

    @Test
    void testWindowSizeMustNotBeSmallerThanMinBackReferenceLength() {
        assertThrows(IllegalArgumentException.class, () -> newParameters(128, 200, 300, 400, 500));
    }
}
