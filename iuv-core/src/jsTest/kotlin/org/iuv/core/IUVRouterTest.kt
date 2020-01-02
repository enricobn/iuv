package org.iuv.core

import kotlin.test.*

class IUVRouterTest {

    @Test
    fun givenARouterWhenISendAGotoWithARouteThatDoesNotExistThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleView(0), true)

        router.add("/simple", SimpleView(1))
        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt()) }

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/dummy", false), model)

        assertNotNull(newModel.errorMessage)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithSimilarPathButNotMatchedThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleView(0), true)

        router.add("/simple", SimpleView(1))
        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt()) }

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple0", false), model)

        assertNotNull(newModel.errorMessage)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithSimilarPathMatchedThenItSucceed() {
        val router = IUVRouter(SimpleView(0), true)

        router.add("/simple", SimpleView(1))
        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt()) }

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple", false), model)

        assertNull(newModel.errorMessage)

        assertEquals(1, (newModel.currentModel as SimpleView.Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithParameterThenTheParameterMustBePresentInSimpleIUV() {
        val router = IUVRouter(SimpleView(0), true)

        router.add("/simple", SimpleView(1))
        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt()) }

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple/5", false), model)

        assertNull(newModel.errorMessage)

        assertEquals(5, (newModel.currentModel as SimpleView.Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithInvalidParameterThenThereIsAnErrorInRouterModel() {
        val router = IUVRouter(SimpleView(0), true)

        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt()) }

        val (model, _) = router.init()

        val (newModel, _) = router.update(Goto("/simple/hello", false), model)

        assertNotNull(newModel.errorMessage)

        assertTrue(newModel.errorMessage!!.toLowerCase().contains("invalid number format"))
    }

    @Test
    fun givenARouteWithParameterWhenISendAGotoWithInvalidParameterAndISendAValidGotoThenThereIsNotAnErrorInRouterModel() {
        val router = IUVRouter(SimpleView(0), true)

        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt()) }

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/simple/hello", false), model)

        assertNotNull(newModel.errorMessage)

        val (newModel1, _) = router.update(Goto("/simple/1", false), newModel)

        assertEquals(1, (newModel1.currentModel as SimpleView.Model).id)
    }

    @Test
    fun givenARouteWithParameterWhenInitTheRouterWithAMatchingPathThenAGotoIsSentWithThePath() {
        val router = IUVRouter(SimpleView(0), true)

        router.add(StringParameterMatcher("/simple")) { SimpleView(it.toInt()) }

        val messages = initRouterAndCaptureMessages(router,
                "http://hello.com#/simple/1")

        assertEquals(Goto("/simple/1", true), messages.first())
    }

    @Test
    fun givenARouterWhenAddingAnInvalidPathThenAnExceptionIsThrown() {
        val router = IUVRouter(SimpleView(0), true)

        var exception: Exception? = null

        try {
            router.add(StringParameterMatcher("simple")) { SimpleView(it.toInt()) }
        } catch (e: Exception) {
            exception = e
        }

        assertNotNull(exception)
    }

    @Test
    fun givenAMapRouteMatcherWhenInitTheRouterWithAMatchingPathThenAGotoIsSentWithThePath() {
        val router = IUVRouter(SimpleView(0), true)

        router.add(MapRouteMatcher("/order/:customerId/:orderId")) { OrderView(it["customerId"]!!, it["orderId"]!!) }

        val messages = initRouterAndCaptureMessages(router,
                "http://hello.com#/order/customer001/order001")

        assertEquals(Goto("/order/customer001/order001", true), messages.first())
    }

    @Test
    fun givenAMapRouteMatcherWhenISendAGotoWithParameterThenTheParametersMustBePresentInView() {
        val router = IUVRouter(SimpleView(0), true)

        router.add(MapRouteMatcher("/order/:customerId/:orderId")) { OrderView(it["customerId"]!!, it["orderId"]!!) }

        val (model, _) = router.init()
        val (newModel, _) = router.update(Goto("/order/customer001/order001", false), model)

        assertNull(newModel.errorMessage)

        assertEquals("customer001", (newModel.currentModel as OrderView.Model).customerId)
        assertEquals("order001", (newModel.currentModel as OrderView.Model).orderId)
    }

    @Test
    fun testMapRouterMatcherLink() {
        val matcher = MapRouteMatcher("/customer/:customerId/order/:orderId")

        val link = matcher.link(linkedMapOf(Pair("customerId", "customer001"), Pair("orderId", "order001")))

        assertEquals("/customer/customer001/order/order001", link)
    }

    private fun initRouterAndCaptureMessages(router: IUVRouter, href: String): MutableList<RouterMessage> {
        val (model, cmd) = router.init(href)

        assertNull(model.errorMessage)

        val messages = mutableListOf<RouterMessage>()

        val messageBus = object : MessageBus<RouterMessage> {
            override fun send(message: RouterMessage) {
                messages += message
            }

        }

        cmd.run(messageBus)
        return messages
    }

}

class SimpleView(private val initialId: Int) : View<SimpleView.Model, SimpleView.Message> {

    class Model(val id: Int)

    interface Message

    override fun init(): Pair<Model, Cmd<Message>> = Pair(Model(initialId), Cmd.none())

    override fun view(model: Model): HTML<Message> = html { }

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
            Pair(model, Cmd.none())

}

class OrderView(private val customerId: String, private val orderId: String) : View<OrderView.Model, OrderView.Message> {

    data class Model(val customerId: String, val orderId: String)

    interface Message

    override fun init(): Pair<Model, Cmd<Message>> = Pair(Model(customerId, orderId), Cmd.none())

    override fun view(model: Model): HTML<Message> = html { }

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> =
            Pair(model, Cmd.none())

}
