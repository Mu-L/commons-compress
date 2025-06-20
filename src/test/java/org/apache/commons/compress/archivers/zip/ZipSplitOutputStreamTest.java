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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import org.apache.commons.compress.AbstractTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class ZipSplitOutputStreamTest extends AbstractTest {

    @Test
    void testCreateSplittedFiles() throws IOException {
        final File testOutputFile = newTempFile("testCreateSplittedFiles.zip");
        final int splitSize = 100 * 1024; /* 100 KB */
        final File fileToTest = getFile("COMPRESS-477/split_zip_created_by_zip/zip_to_compare_created_by_zip.zip");
        try (ZipSplitOutputStream zipSplitOutputStream = new ZipSplitOutputStream(testOutputFile, splitSize);
                InputStream inputStream = Files.newInputStream(fileToTest.toPath())) {
            IOUtils.copy(inputStream, zipSplitOutputStream);
        }
        File zipFile = new File(getTempDirFile().getPath(), "testCreateSplittedFiles.z01");
        assertEquals(zipFile.length(), splitSize);
        zipFile = new File(getTempDirFile().getPath(), "testCreateSplittedFiles.z02");
        assertEquals(zipFile.length(), splitSize);
        zipFile = new File(getTempDirFile().getPath(), "testCreateSplittedFiles.z03");
        assertEquals(zipFile.length(), splitSize);
        zipFile = new File(getTempDirFile().getPath(), "testCreateSplittedFiles.z04");
        assertEquals(zipFile.length(), splitSize);
        zipFile = new File(getTempDirFile().getPath(), "testCreateSplittedFiles.z05");
        assertEquals(zipFile.length(), splitSize);
        zipFile = new File(getTempDirFile().getPath(), "testCreateSplittedFiles.zip");
        assertEquals(zipFile.length(), fileToTest.length() + 4 - splitSize * 5);
    }

    @Test
    void testSplitZipBeginsWithZipSplitSignature() throws IOException {
        final File tempFile = createTempFile("temp", "zip");
        try (ZipSplitOutputStream is = new ZipSplitOutputStream(tempFile, 100 * 1024L);
                InputStream inputStream = Files.newInputStream(tempFile.toPath())) {
            final byte[] buffer = new byte[4];
            inputStream.read(buffer);
            assertEquals(ByteBuffer.wrap(ZipArchiveOutputStream.DD_SIG).getInt(), ByteBuffer.wrap(buffer).getInt());
        }
    }

    @Test
    void testThrowsExceptionIfSplitSizeIsTooLarge() {
        assertThrows(IllegalArgumentException.class, () -> new ZipSplitOutputStream(createTempFile("temp", "zip"), 4 * 1024 * 1024 * 1024L));
    }

    @Test
    void testThrowsExceptionIfSplitSizeIsTooSmall() {
        assertThrows(IllegalArgumentException.class, () -> new ZipSplitOutputStream(createTempFile("temp", "zip"), 64 * 1024 - 1));
    }

    @Test
    void testThrowsIfUnsplittableSizeLargerThanSplitSize() throws IOException {
        final long splitSize = 100 * 1024;
        final ZipSplitOutputStream output = new ZipSplitOutputStream(createTempFile("temp", "zip"), splitSize);
        assertThrows(IllegalArgumentException.class, () -> output.prepareToWriteUnsplittableContent(splitSize + 1));
    }
}
