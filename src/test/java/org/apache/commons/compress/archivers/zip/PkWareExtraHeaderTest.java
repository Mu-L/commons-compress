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
package org.apache.commons.compress.archivers.zip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.compress.archivers.zip.PKWareExtraHeader.EncryptionAlgorithm;
import org.apache.commons.compress.archivers.zip.PKWareExtraHeader.HashAlgorithm;
import org.junit.jupiter.api.Test;

class PkWareExtraHeaderTest {

    @Test
    void testEncryptionAlgorithm() {
        final String name = "AES256";
        final int code = EncryptionAlgorithm.AES256.getCode();
        final EncryptionAlgorithm e = EncryptionAlgorithm.valueOf(name);
        assertEquals(code, e.getCode());
        assertNotNull(e);
    }

    @Test
    void testHashAlgorithm() {
        final String name = "SHA256";
        final int code = HashAlgorithm.SHA256.getCode();
        final HashAlgorithm e = HashAlgorithm.valueOf(name);
        assertEquals(code, e.getCode());
        assertNotNull(e);
    }

}
