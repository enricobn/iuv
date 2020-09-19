import kotlinx.serialization.InternalSerializationApi
import org.iuv.core.IUVApplication
import org.iuv.core.SnabbdomRenderer
import org.iuv.todomvc.TodoMVC

@InternalSerializationApi
fun main() {
    val renderer = SnabbdomRenderer()
    val application = IUVApplication(TodoMVC, renderer)
    application.run()
}