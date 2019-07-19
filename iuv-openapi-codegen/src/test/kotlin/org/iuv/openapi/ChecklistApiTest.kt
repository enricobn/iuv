package org.iuv.openapi

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.io.StringWriter

class ChecklistApiTest {
    private val context = OpenAPIWriteContext("org.checklist.web.controller",
            "org.checklist.ui.api", "org.checklist.shared.models")

    @Test
    fun components() {
        val server = OpenAPIReader.parse(getResource("/checklist.yaml"), "Checklist",
                context, false)

        if (server == null) {
            fail()
            return
        }

        val sw = StringWriter()
        sw.use {
            OpenAPIReader.runTemplate(getResource("/openapi/templates/components.mustache"), server,
                    context, it)
            assertEquals("package org.checklist.shared.models\n" +
                    "\n" +
                    "import kotlinx.serialization.Serializable\n" +
                    "import kotlinx.serialization.SerialName\n" +
                    "\n" +
                    "@Serializable\n" +
                    "data class ChecklistDefDTO(\n" +
                    "\n" +
                    "    @SerialName(\"id\")\n" +
                    "    val id : String,\n" +
                    "\n" +
                    "    @SerialName(\"title\")\n" +
                    "    val title : String,\n" +
                    "\n" +
                    "    @SerialName(\"children\")\n" +
                    "    val children : List<ChecklistDefDTO>,\n" +
                    "\n" +
                    "    @SerialName(\"editable\")\n" +
                    "    val editable : Boolean,\n" +
                    "\n" +
                    "    @SerialName(\"threeState\")\n" +
                    "    val threeState : Boolean? = false,\n" +
                    "\n" +
                    "    @SerialName(\"auditInfo\")\n" +
                    "    val auditInfo : AuditInfoDTO? = null\n" +
                    ")\n" +
                    "\n" +
                    "@Serializable\n" +
                    "data class ChecklistDTO(\n" +
                    "\n" +
                    "    @SerialName(\"id\")\n" +
                    "    val id : String,\n" +
                    "\n" +
                    "    @SerialName(\"title\")\n" +
                    "    val title : String,\n" +
                    "\n" +
                    "    @SerialName(\"status\")\n" +
                    "    val status : RunStatus,\n" +
                    "\n" +
                    "    @SerialName(\"children\")\n" +
                    "    val children : List<ChecklistDTO>,\n" +
                    "\n" +
                    "    @SerialName(\"editable\")\n" +
                    "    val editable : Boolean,\n" +
                    "\n" +
                    "    @SerialName(\"threeState\")\n" +
                    "    val threeState : Boolean? = true,\n" +
                    "\n" +
                    "    @SerialName(\"auditInfo\")\n" +
                    "    val auditInfo : AuditInfoDTO? = null\n" +
                    ")\n" +
                    "\n" +
                    "enum class RunStatus {\n" +
                    "    NOT_RUN,\n" +
                    "    FAILED,\n" +
                    "    SUCCEEDED\n" +
                    "}\n" +
                    "\n" +
                    "@Serializable\n" +
                    "data class AuditInfoDTO(\n" +
                    "\n" +
                    "    @SerialName(\"createDate\")\n" +
                    "    val createDate : String? = null,\n" +
                    "\n" +
                    "    @SerialName(\"lastChangeDate\")\n" +
                    "    val lastChangeDate : String? = null\n" +
                    ")", sw.toString())
        }
    }

}

private fun getResource(resource: String) = OpenAPIReaderTest::class.java.getResource(resource)

