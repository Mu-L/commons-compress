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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.AbstractTempDirTest;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;

class ScatterSampleTest extends AbstractTempDirTest {

    private void checkFile(final File result) throws IOException {
        try (ZipFile zipFile = ZipFile.builder().setFile(result).get()) {
            final ZipArchiveEntry archiveEntry1 = zipFile.getEntries().nextElement();
            assertEquals("test1.xml", archiveEntry1.getName());
            try (InputStream inputStream = zipFile.getInputStream(archiveEntry1)) {
                final byte[] b = new byte[6];
                final int i = IOUtils.readFully(inputStream, b);
                assertEquals(5, i);
                assertEquals('H', b[0]);
                assertEquals('o', b[4]);
            }
        }
        assertTrue(result.delete());
    }

    private void createFile(final File result) throws IOException, ExecutionException, InterruptedException {
        final ScatterSample scatterSample = new ScatterSample(this);
        final ZipArchiveEntry archiveEntry = new ZipArchiveEntry("test1.xml");
        archiveEntry.setMethod(ZipEntry.DEFLATED);
        final InputStreamSupplier supp = () -> new ByteArrayInputStream("Hello".getBytes());

        scatterSample.addEntry(archiveEntry, supp);
        try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(result)) {
            scatterSample.writeTo(zipArchiveOutputStream);
        }
    }

    @Test
    void testSample() throws Exception {
        final File result = createTempFile("testSample", "fe");
        createFile(result);
        checkFile(result);
    }
}
