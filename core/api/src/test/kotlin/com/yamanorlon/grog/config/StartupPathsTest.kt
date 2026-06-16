package com.yamanorlon.grog.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class StartupPathsTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `ensureWritableDirectories creates missing directories`() {
        val openApiDir = tempDir.resolve("docs").toString()
        val logsDir = tempDir.resolve("logs").toString()

        val resolved = StartupPaths.ensureWritableDirectories(openApiDir, logsDir)

        assertTrue(Files.isDirectory(resolved.openApiOutputDir))
        assertTrue(Files.isWritable(resolved.openApiOutputDir))
        assertEquals(openApiDir, resolved.openApiOutputDir.toString())
        assertTrue(Files.isDirectory(resolved.logsDir))
    }

    @Test
    fun `resolvePath normalizes relative paths against working directory`() {
        val resolved = StartupPaths.resolvePath("./docs")
        assertTrue(resolved.isAbsolute)
        assertTrue(resolved.endsWith("docs"))
    }
}
