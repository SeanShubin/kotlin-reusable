package com.seanshubin.kotlin.reusable.exec

import java.nio.file.Path

interface Exec {
    data class Result(
        val exitCode: Int,
        val output: String
    ) {
        val success: Boolean get() = exitCode == 0
    }

    fun exec(workingDirectory: Path, command: List<String>): String
    fun execForResult(workingDirectory: Path, command: List<String>): Result
}

class ExecException(
    val command: List<String>,
    val workingDirectory: Path,
    val exitCode: Int,
    val output: String
) : RuntimeException(
    "Command ${command.joinToString(" ")} failed with exit code $exitCode in $workingDirectory\nOutput:\n$output"
)
