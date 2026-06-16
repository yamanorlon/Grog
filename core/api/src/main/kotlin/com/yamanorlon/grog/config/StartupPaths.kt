package com.yamanorlon.grog.config

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import org.slf4j.LoggerFactory
import java.nio.file.AccessDeniedException

object StartupPaths {
    private val logger = LoggerFactory.getLogger(StartupPaths::class.java)

    data class ResolvedPaths(
        val openApiOutputDir: Path,
        val logsDir: Path?,
    )

    fun ensureWritableDirectories(openApiOutputDir: String, logsDir: String?): ResolvedPaths {
        val resolvedOpenApiDir = resolvePath(openApiOutputDir)
        logger.info("Путь к OpenApi документации: {}", resolvedOpenApiDir.toAbsolutePath())
        ensureDirectory(resolvedOpenApiDir, "OpenAPI output")

        val resolvedLogsDir = logsDir?.takeIf { it.isNotBlank() }?.let { configured ->
            val path = resolvePath(configured)
            logger.info("Путь к логам приложения: {}", path.toAbsolutePath())
            ensureDirectory(path, "logs")
            path
        }

        return ResolvedPaths(resolvedOpenApiDir, resolvedLogsDir)
    }

    fun resolvePath(path: String): Path {
        val candidate = Paths.get(path)
        return if (candidate.isAbsolute) {
            candidate.normalize()
        } else {
            Paths.get(System.getProperty("user.dir")).resolve(candidate).normalize()
        }
    }

    private fun ensureDirectory(path: Path, label: String) {
        logger.info("Попытка поиска каталога: {}", label)
        try {
            if (Files.exists(path)) {
                if (!Files.isDirectory(path)) {
                    throw IllegalStateException("$label путь существует но не является каталогом: $path")
                }
                if (!Files.isWritable(path)) {
                    throw IllegalStateException("$label в данном каталоге отсутствуют права на запись: ${path.toAbsolutePath()}")
                }
                logger.info("{} каталог уже существует: {}", label, path.toAbsolutePath())
                return
            }

            Files.createDirectories(path)
            logger.info("{} каталог создан: {}", label, path.toAbsolutePath())
        } catch (ex: AccessDeniedException) {
            logger.error("Проблема с правами при создании {} по пути {}", label, path.toAbsolutePath(), ex)
            throw IllegalStateException(
                "Проблема с правами при создании $label по пути: ${path.toAbsolutePath()}",
                ex,
            )
        } catch (ex: IllegalStateException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Ошибка при создании каталога {} по пути {}", label, path.toAbsolutePath(), ex)
            throw IllegalStateException(
                "Ошибка при создании каталога $label по пути: ${path.toAbsolutePath()}",
                ex,
            )
        }
    }
}
