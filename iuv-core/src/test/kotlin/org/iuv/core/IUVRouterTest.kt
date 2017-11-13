package org.iuv.core

import kotlin.browser.window
import kotlin.test.*

class Model(val id: Int)

interface Message

class SimpleIUV(val initialId: Int) : IUV<Model, Message> {

    override fun init() : Pair<Model, Cmd<Message>> = Pair(Model(initialId), Cmd.none())

    override fun view(model: Model): HTML<Message> =
            html {+"SimpleIUV"}

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
            Pair(model, Cmd.none())

}

class IUVRouterTest {

    @Test
    fun givenARouterWhenISendAGotoWithARouteThatDoesNotExistsThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("simple1", SimpleIUV(1))
        router.add("simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/dummy", false), model)

        assertNotNull(newModel.errorMessage)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithSimilarPathButNotMatchedThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("simple1", SimpleIUV(1))
        router.add("simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple0", false), model)

        assertNotNull(newModel.errorMessage)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithSimilarPathMatchedThenItSucceed() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("simple1", SimpleIUV(1))
        router.add("simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple1", false), model)

        assertEquals(1, (newModel.currentIUVModel as Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithParameterThenTheParameterMustBePresentInSimpleIUV() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("simple1", SimpleIUV(1))
        router.add("simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple/5", false), model)

        assertEquals(5, (newModel.currentIUVModel as Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithInvalidParameterThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("simple") { SimpleIUV(it.first().toInt()) }

        val (model, _) = router.init()

        assertNull(model.errorMessage)

        val (newModel, _) = router.update(Goto("/simple/hello", false), model)

        assertNotNull(newModel.errorMessage)

        assertTrue(newModel.errorMessage!!.toLowerCase().contains("invalid number format"))
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithInvalidParameterAndISendAValidGotoThenThereIsNotAnErrorInRouterModel() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("simple") { SimpleIUV(it.first().toInt()) }

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple/hello", false), model)

        assertNotNull(newModel.errorMessage)

        val (newModel1, _) = router.update(Goto("/simple/1", false), newModel)

        assertEquals(1, (newModel1.currentIUVModel as Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenIInitTheRouterWithAMatchingPathThenAGotoIsSentWithThePath() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("simple") { SimpleIUV(it.first().toInt()) }

        val (model, cmd) = router.init("http://hello.com#/simple/1")

        assertNull(model.errorMessage)

        assertNull(model.path)

        val messages = mutableListOf<RouterMessage>()

        val messagetBus = object : MessageBus<RouterMessage> {
            override fun send(message: RouterMessage) {
                messages += message
            }

        }

        cmd.run(messagetBus)

        assertEquals(Goto("/simple/1", true), messages.first())
    }
}