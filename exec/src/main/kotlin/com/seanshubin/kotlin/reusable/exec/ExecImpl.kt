package com.seanshubin.kotlin.reusable.exec

import com.seanshubin.kotlin.reusable.di.contract.ProcessBuilderContract
import com.seanshubin.kotlin.reusable.di.delegate.ProcessBuilderDelegate
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path

class ExecImpl : Exec {
    override fun exec(workingDirectory: Path, command: List<String>): String {
        val result = execForResult(workingDirectory, command)
        if (result.exitCode != 0) {
            throw ExecException(command, workingDirectory, result.exitCode, result.output)
        }
        return result.output
    }

    override fun execForResult(workingDirectory: Path, command: List<String>): Exec.Result {
        val processBuilder: ProcessBuilderContract = ProcessBuilderDelegate(command)
        processBuilder.directory(workingDirectory.toFile())
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val output = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.readText()
        }
        val exitCode = process.waitFor()

        return Exec.Result(exitCode, output)
    }
}
