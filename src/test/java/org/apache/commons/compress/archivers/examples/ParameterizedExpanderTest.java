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
package org.apache.commons.compress.archivers.examples;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.apache.commons.compress.AbstractTest;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runners.Parameterized.Parameters;

class ParameterizedExpanderTest extends AbstractTest {

    // 7z and ZIP using ZipFile is in ExpanderTest
    @Parameters(name = "format={0}")
    public static Stream<Arguments> data() {
        return Stream.of(Arguments.of("tar"), Arguments.of("cpio"), Arguments.of("zip"));
    }

    private File archive;

    @ParameterizedTest
    @MethodSource("data")
    public void archiveInputStreamVersion(final String format) throws Exception {
        // TODO How to parameterize a BeforeEach method?
        setUp(format);
        try (InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath()));
                ArchiveInputStream<?> ais = ArchiveStreamFactory.DEFAULT.createArchiveInputStream(format, i)) {
            new Expander().expand(ais, tempResultDir);
        }
        verifyTargetDir();
    }

    private void assertHelloWorld(final String fileName, final String suffix) throws IOException {
        assertTrue(new File(tempResultDir, fileName).isFile(), fileName + " does not exist");
        final byte[] expected = ("Hello, world " + suffix).getBytes(UTF_8);
        final byte[] actual = Files.readAllBytes(tempResultDir.toPath().resolve(fileName));
        assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void channelVersion(final String format) throws Exception {
        // TODO How to parameterize a BeforeEach method?
        setUp(format);
        try (SeekableByteChannel c = FileChannel.open(archive.toPath(), StandardOpenOption.READ)) {
            new Expander().expand(format, c, tempResultDir);
        }
        verifyTargetDir();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void fileVersion(final String format) throws Exception {
        // TODO How to parameterize a BeforeEach method?
        setUp(format);
        new Expander().expand(format, archive, tempResultDir);
        verifyTargetDir();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void fileVersionWithAutoDetection(final String format) throws Exception {
        // TODO How to parameterize a BeforeEach method?
        setUp(format);
        new Expander().expand(archive, tempResultDir);
        verifyTargetDir();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void inputStreamVersion(final String format) throws Exception {
        // TODO How to parameterize a BeforeEach method?
        setUp(format);
        try (InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath()))) {
            new Expander().expand(format, i, tempResultDir);
        }
        verifyTargetDir();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void inputStreamVersionWithAutoDetection(final String format) throws Exception {
        // TODO How to parameterize a BeforeEach method?
        setUp(format);
        try (InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath()))) {
            new Expander().expand(i, tempResultDir);
        }
        verifyTargetDir();
    }

    public void setUp(final String format) throws Exception {
        archive = newTempFile("test." + format);
        final File dummy = newTempFile("x");
        try (OutputStream o = Files.newOutputStream(dummy.toPath())) {
            o.write(new byte[14]);
        }
        try (ArchiveOutputStream<ArchiveEntry> aos = ArchiveStreamFactory.DEFAULT.createArchiveOutputStream(format, Files.newOutputStream(archive.toPath()))) {
            aos.putArchiveEntry(aos.createArchiveEntry(getTempDirFile().toPath(), "a"));
            aos.closeArchiveEntry();
            aos.putArchiveEntry(aos.createArchiveEntry(getTempDirFile(), "a/b"));
            aos.closeArchiveEntry();
            aos.putArchiveEntry(aos.createArchiveEntry(getTempDirFile(), "a/b/c"));
            aos.closeArchiveEntry();
            aos.putArchiveEntry(aos.createArchiveEntry(dummy, "a/b/d.txt"));
            aos.writeUtf8("Hello, world 1");
            aos.closeArchiveEntry();
            aos.putArchiveEntry(aos.createArchiveEntry(dummy, "a/b/c/e.txt"));
            aos.writeUtf8("Hello, world 2");
            aos.closeArchiveEntry();
            aos.finish();
        }
    }

    private void verifyTargetDir() throws IOException {
        assertTrue(new File(tempResultDir, "a").isDirectory(), "a has not been created");
        assertTrue(new File(tempResultDir, "a/b").isDirectory(), "a/b has not been created");
        assertTrue(new File(tempResultDir, "a/b/c").isDirectory(), "a/b/c has not been created");
        assertHelloWorld("a/b/d.txt", "1");
        assertHelloWorld("a/b/c/e.txt", "2");
    }

}
