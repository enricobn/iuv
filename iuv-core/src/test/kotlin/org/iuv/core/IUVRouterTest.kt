package org.iuv.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class Model

interface Message

class SimpleIUV(val id : Int) : IUV<Model, Message> {
    override fun init() : Pair<Model, Cmd<Message>> = Pair(Model(), Cmd.none())

    override fun view(model: Model): HTML<Message>  =
            html {
            }

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
            Pair(Model(), Cmd.none())
}

class IUVRouterTest {

    @Test
    fun givenARouterWhenISendAGotoWithARouteThatDoesNotExistsThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("/simple1", SimpleIUV(1))
        router.add("/simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/dummy"), model)

        assertNotNull(newModel.errorMessage)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithSimilarPathButNotMatchedThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("/simple1", SimpleIUV(1))
        router.add("/simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple0"), model)

        assertNotNull(newModel.errorMessage)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithSimilarPathMatchedThenItSucceed() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("/simple1", SimpleIUV(1))
        router.add("/simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple1"), model)

        assertEquals(1, (newModel.currentIUV!!.childIUV as SimpleIUV).id)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithParameterThenTheParameterMustBePresentInSimpleIUV() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("/simple1", SimpleIUV(1))
        router.add("/simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple/5"), model)

        assertEquals(5, (newModel.currentIUV!!.childIUV as SimpleIUV).id)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithInvalidParameterThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleIUV(0), true)

        router.add("/simple") { SimpleIUV(it.first().toInt())}

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple/hello"), model)

        assertNotNull(newModel.errorMessage)
    }
}