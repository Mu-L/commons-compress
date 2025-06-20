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

package org.apache.commons.compress;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link MemoryLimitException}.
 */
class MemoryLimitExceptionTest {

    @Test
    void testAccessorsCause() {
        final IOException ioe = new IOException();
        final MemoryLimitException e = new MemoryLimitException(1, 2, (Throwable) ioe);
        assertEquals(1, e.getMemoryNeededInKb());
        assertEquals(2, e.getMemoryLimitInKb());
        assertSame(ioe, e.getCause());
    }

    @Test
    void testAccessorsCauseDepreacted() {
        final IOException ioe = new IOException();
        @SuppressWarnings("deprecation")
        final MemoryLimitException e = new MemoryLimitException(1, 2, ioe);
        assertEquals(1, e.getMemoryNeededInKb());
        assertEquals(2, e.getMemoryLimitInKb());
        assertSame(ioe, e.getCause());
    }

    @Test
    void testAccessorsLimit() {
        final MemoryLimitException e = new MemoryLimitException(1, 2);
        assertEquals(1, e.getMemoryNeededInKb());
        assertEquals(2, e.getMemoryLimitInKb());
    }
}
