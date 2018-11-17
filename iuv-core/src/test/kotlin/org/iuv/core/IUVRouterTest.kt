package org.iuv.core

import kotlin.test.*

// Model
class Model(val id: Int)

// Messages
interface Message

// IUV
class SimpleView(val initialId: Int) : View<Model, Message> {

    override fun init() : Pair<Model, Cmd<Message>> = Pair(Model(initialId), Cmd.none())

    override fun view(model: Model): HTML<Message> =
            html {+"SimpleView"}

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
            Pair(model, Cmd.none())

}

class IUVRouterTest {

    @Test
    fun givenARouterWhenISendAGotoWithARouteThatDoesNotExistsThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleView(0), true)

        router.add("/simple", SimpleView(1))
        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/dummy", false), model)

        assertNotNull(newModel.errorMessage)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithSimilarPathButNotMatchedThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleView(0), true)

        router.add("/simple", SimpleView(1))
        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple0", false), model)

        assertNotNull(newModel.errorMessage)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithSimilarPathMatchedThenItSucceed() {
        val router = IUVRouter(SimpleView(0), true)

        router.add("/simple", SimpleView(1))
        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple", false), model)

        assertEquals(1, (newModel.currentModel as Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithParameterThenTheParameterMustBePresentInSimpleIUV() {
        val router = IUVRouter(SimpleView(0), true)

        router.add("/simple", SimpleView(1))
        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple/5", false), model)

        assertEquals(5, (newModel.currentModel as Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithInvalidParameterThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleView(0), true)

        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt())}

        val (model, _) = router.init()

        val (newModel, _) = router.update(Goto("/simple/hello", false), model)

        assertNotNull(newModel.errorMessage)

        assertTrue(newModel.errorMessage!!.toLowerCase().contains("invalid number format"))
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithInvalidParameterAndISendAValidGotoThenThereIsNotAnErrorInRouterModel() {
        val router = IUVRouter(SimpleView(0), true)

        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple/hello", false), model)

        assertNotNull(newModel.errorMessage)

        val (newModel1, _) = router.update(Goto("/simple/1", false), newModel)

        assertEquals(1, (newModel1.currentModel as Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenInitTheRouterWithAMatchingPathThenAGotoIsSentWithThePath() {
        val router = IUVRouter(SimpleView(0), true)

        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt())}

        val (model, cmd) = router.init("http://hello.com#/simple/1")

        assertNull(model.errorMessage)

        val messages = mutableListOf<RouterMessage>()

        val messagetBus = object : MessageBus<RouterMessage> {
            override fun send(message: RouterMessage) {
                messages += message
            }

        }

        cmd.run(messagetBus)

        assertEquals(Goto("/simple/1", true), messages.first())
    }

    @Test
    fun givenARouterWhenAddingAnInvalidPathThenAnExceptionIsThrown() {
        val router = IUVRouter(SimpleView(0), true)

        var exception: Exception? = null

        try {
            router.add(StringParameterMatcher("simple")) { SimpleView(it.toInt())}
        } catch (e: Exception) {
            exception = e
        }

        assertNotNull(exception)
    }

}