package com.coditory.klog.text.plain

import java.io.File
import java.net.URL

internal object ClassNameLister {
    fun getClassNamesFromPackage(
        packageName: String,
        recursive: Boolean = true,
        max: Int = Int.MAX_VALUE,
    ): List<String> {
        val classLoader = Thread.currentThread().contextClassLoader!!
        val path = packageName.replace('.', '/')
        if (!recursive) {
            val resource = classLoader.getResource(path)
            if (resource != null) {
                val file = File(resource.file)
                val classes = mutableListOf<String>()
                findClasses(file, packageName, max, classes)
                return classes
            }
        }
        val resources = classLoader.getResources(path)
        val dirs = mutableListOf<File>()
        while (resources.hasMoreElements()) {
            val resource: URL = resources.nextElement()
            dirs.add(File(resource.file))
        }
        val classes = mutableListOf<String>()
        for (directory in dirs) {
            findClasses(directory, packageName, max, classes)
        }
        return classes
    }

    private fun findClasses(
        directory: File,
        packageName: String,
        max: Int,
        result: MutableList<String>,
    ) {
        if (!directory.exists() || result.size == max) {
            return
        }
        val files = directory.listFiles()!!
        for (file in files) {
            if (file.isDirectory) {
                findClasses(file, packageName + "." + file.name, max, result)
            } else if (file.name.endsWith(".class")) {
                result.addLast(packageName + '.' + file.name.substring(0, file.name.length - 6))
            }
        }
    }
}
