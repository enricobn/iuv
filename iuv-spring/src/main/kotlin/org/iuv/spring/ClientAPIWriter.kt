package org.iuv.spring

import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.EnumSerializer
import kotlinx.serialization.internal.IntSerializer
import kotlinx.serialization.internal.UnitSerializer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.superclasses

class ClientAPIWriter(private val servlet: ServiceVOServlet, private val baseURL: String) {
    private val imports = mutableSetOf("org.iuv.core.Http")
    private val interfaces = mutableSetOf<String>()

    fun write(sb: StringBuilder) {
        val functionsSB = StringBuilder()

        servlet.routes.forEach { writeFunction(functionsSB, it) }

        imports.sorted().forEach { sb.appendln("import $it") }

        sb.appendln()

        val className = servlet::class.simpleName + "Client"

        sb.append("class $className")

        if (interfaces.isNotEmpty()) {
            sb.append(" : ")
            sb.append(interfaces.joinToString(", "))
        }

        sb.appendln(" {")

        sb.append(functionsSB)

        sb.appendln()

        sb.appendln("}")
    }
    
    private fun writeFunction(sb: StringBuilder, route: Route) {

        servlet::class.superclasses.filter {
            hasFunction(it, route.function)
        }.forEach {
            imports.add(it.qualifiedName!!)
            interfaces.add(simpleName(it))
        }

        sb.appendln()
        sb.indent().append("override fun ")
        sb.append(route.function.name)
        sb.append("(")

        sb.append(route.function.parameters.drop(1).joinToString(", ") {
            val type = toString(it.type)
            imports.add(type)
            "${it.name}: ${ simpleName(type) }"
        })

        /*findById(id: ChecklistTaskVOId): Task<String, ChecklistVO> =
                Http.GET(ChecklistEditServiceVO.findById(id), ChecklistVO::class.serializer())*/
        sb.appendln(") =")

        val pathComponents = route.routeMatcher.expComponents.map {
            if (it.startsWith("{")) {
                val pathVariableName = it.substring(1, it.length - 1)

                val pathVariableParameter =
                        route.pathVariableParameters.firstOrNull { it.variableName == pathVariableName }
                                ?: throw Exception("Cannot find parameter with name '$pathVariableName'.")

                "\$" + pathVariableParameter.parameterName
            } else
                it
        }

        sb.indent(2).append("Http.${route.methods[0].name}(")
        sb.append("\"$baseURL/${pathComponents.joinToString("/")}\"")
        sb.append(", ")

        val kSerializer = route.routeSerializer.value.objectInstance!!.serializer

        sb.append(getSerializer(kSerializer))

        // TODO body

        sb.appendln(")")

    }

    private fun StringBuilder.indent(num: Int = 1) : StringBuilder {
        IntRange(1, num).forEach { append("    ") }
        return this
    }

    private fun hasFunction(kClass: KClass<*>, fn: KFunction<*>) =
        kClass.declaredFunctions.firstOrNull { equals(it, fn) } != null

    private fun equals(fn1: KFunction<*>, fn2: KFunction<*>) =
        fn1.name == fn2.name &&
        fn1.parameters.size == fn2.parameters.size &&
        fn1.parameters
                .zip(fn2.parameters)
                .drop(1) // the first parameter is "this"
                .all { (p1, p2) -> p1.name == p2.name && p1.type == p2.type }

    private fun toString(type: KType): String {
        val s = type.toString()

        // to remove the comment for alias types
        val space = s.indexOf(' ')

        return if (space >= 0)
            s.substring(0, space)
        else
            s
    }

    private fun getSerializer(kSerializer: KSerializer<*>) : String? {
        when (kSerializer) {
            is IntSerializer -> {
                imports.add(IntSerializer::class.qualifiedName!!)
                return "IntSerializer"
            }
            is UnitSerializer -> {
                imports.add(UnitSerializer::class.qualifiedName!!)
                return "UnitSerializer"
            }
            is ArrayListSerializer<*> -> {
                imports.add(ArrayListSerializer::class.qualifiedName!!)
                val elementSerializer = getSerializer(kSerializer.elementSerializer)
                return "ArrayListSerializer($elementSerializer)"
            }
            is EnumSerializer<*> -> return "EnumSerializer(${kSerializer.descriptor.name})" // Is it correct?
            else -> {
                val indexOfCustomSerializer = kSerializer.toString().indexOf("${'$'}${'$'}serializer")
                if (indexOfCustomSerializer >= 0) {
                    val className = kSerializer.toString().substring(0, indexOfCustomSerializer)
                    imports.add(className)
                    return "${simpleName(className)}::class.serializer()"
                }
            }
        }
        return null
    }

    private fun simpleName(kClass: KClass<*>) = simpleName(kClass.qualifiedName!!)

    private fun simpleName(kClass: String): String {
        val dot = kClass.lastIndexOf('.')
        return kClass.substring(dot + 1)
    }

}