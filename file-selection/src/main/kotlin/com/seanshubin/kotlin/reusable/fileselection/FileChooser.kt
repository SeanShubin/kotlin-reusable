package com.seanshubin.kotlin.reusable.fileselection

import java.nio.file.Path

interface FileChooser {
    fun choose(fileSelection: FileSelection): List<Path>
}
