package org.iuv.spring

class SimpleRouteMatcher(private val expression: String) {
    val expComponents = splitAbsolutePath(expression)

    fun matches(absolutePath: String?) : Boolean {

        if (absolutePath == null)
            return expression.isEmpty()
        else {
            val pathComponents = splitAbsolutePath(absolutePath)

            if (pathComponents.size != expComponents.size) return false

            return expComponents.zip(pathComponents).all { it.first.startsWith("{") || it.first == it.second }
        }
    }

    fun pathVariables(absolutePath: String?): Map<String,String> {
        if (absolutePath == null)
            return mapOf()
        else {
            val pathComponents = splitAbsolutePath(absolutePath)

            return expComponents
                    .zip(pathComponents)
                    .filter { it.first.startsWith("{") }
                    .map { Pair(it.first.substring(1, it.first.lastIndex), it.second) }
                    .toMap()
        }
    }

    fun exactMatch(absolutePath: String?): Boolean {
        if (absolutePath == null)
            return expression.isEmpty()
        else
            return expComponents == splitAbsolutePath(absolutePath)
    }

    private fun splitAbsolutePath(absolutePath: String): List<String> {
        if (absolutePath == "/" || absolutePath.isEmpty())
            return emptyList()

        val path =
                if (absolutePath.endsWith("/"))
                    absolutePath.substring(1, absolutePath.lastIndex)
                else if (absolutePath.startsWith("/"))
                    absolutePath.substring(1)
                else
                    absolutePath

        return path.split(("/"))
    }

}