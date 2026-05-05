package com.seanshubin.kotlin.reusable.fileselection

import java.nio.file.Path

data class FileSelection(
    val baseDir: Path,
    val includePatterns: List<String>,
    val excludePatterns: List<String> = emptyList(),
    val skipDirectoryPatterns: List<String> = emptyList()
)
