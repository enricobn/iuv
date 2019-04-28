package org.iuv.spring

class ServletRouteMatcher(private val expression: String) {
    val expComponents = splitAbsolutePath(expression)
    val pathVariableNames = expComponents
            .filter { it.startsWith("{") }
            .map { it.substring(1, it.lastIndex) }

    fun matches(absolutePath: String?) : Boolean =

        if (absolutePath == null)
            expression.isEmpty()
        else {
            val pathComponents = splitAbsolutePath(absolutePath)

            if (pathComponents.size != expComponents.size) {
                false
            } else {
                expComponents.zip(pathComponents).all { it.first.startsWith("{") || it.first == it.second }
            }
        }

    fun pathVariables(absolutePath: String?): Map<String,String> =
        if (absolutePath == null)
            emptyMap()
        else {
            val pathComponents = splitAbsolutePath(absolutePath)

            pathVariableNames
                    .zip(pathComponents)
                    .map { Pair(it.first, it.second) }
                    .toMap()
        }

    fun exactMatch(absolutePath: String?): Boolean =
        if (absolutePath == null)
            expression.isEmpty()
        else
            expComponents == splitAbsolutePath(absolutePath)

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