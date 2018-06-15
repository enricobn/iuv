package org.iuv.examples.mario

import org.iuv.core.*
import kotlin.browser.window
import kotlin.math.max

private const val marioSize = 64

class MarioView : View<MarioView.Model, MarioView.Message> {
    // MODEL
    data class Keys(val x: Double, val y: Double)

    data class Model(val x : Double,
                     val y : Double,
                     val keys: Keys = Keys(0.0, 0.0),
                     val dir : Direction = Direction.Right,
                     val vx : Double = 0.0,
                     val vy : Double = 0.0)

    // MESSAGES
    interface Message

    data class KeyDown(val keyCode: Int) : Message
    data class KeyUp(val keyCode: Int) : Message
    data class Frame(val diff: Double) : Message

    enum class Direction { Left, Right }

    override fun subscriptions(model: Model): Sub<Message> {
        return Sub(super.subscriptions(model),
                DocumentEventSubFactoryImpl.keydown({ KeyDown(it.keyCode) }),
                DocumentEventSubFactoryImpl.keyup({ KeyUp(it.keyCode) }),
                DocumentEventSubFactoryImpl.animationFrame(::Frame)
        )
    }

    override fun init(): Pair<Model, Cmd<Message>> = Pair(Model(100.0, (window.innerHeight - marioSize).toDouble()), Cmd.none())

    override fun update(message: Message, model: Model): Pair<Model, Cmd<Message>> {
        val newModel = when (message) {
            is KeyDown -> model.copy(keys = applyKey(1.0, message.keyCode, model.keys))
            is KeyUp -> model.copy(keys = applyKey(0.0, message.keyCode, model.keys))
            is Frame -> {
                physics(message.diff / 10, walk(jump(gravity(message.diff / 10, model))))
                //.copy(x = model.x + model.keys.x, y = model.y + model.keys.y)
            }
            else -> model
        }
        return Pair(newModel, Cmd.none())
    }

    override fun view(model: Model): HTML<Message> {
        val srcImage = srcImage(model)

        val groundY =
                62 - window.innerHeight / 2

        val x = model.x

        val y = window.innerHeight - model.y + groundY

        return html {
            img {
                src = srcImage
                style = "width: ${marioSize}px; height: ${marioSize}px; transform: matrix(1, 0, 0, 1, $x, $y);"
            }
        }
    }

    private fun srcImage(model: Model): String {
        val verb =
            when {
                model.y > 0 -> "jump"
                model.vx.toInt() != 0 -> "walk"
                else -> "stand"
            }

        val dir = when (model.dir) {
            Direction.Left ->
                "left"

            Direction.Right ->
                "right"
        }

        return "imgs/mario/$verb/$dir.gif"
    }

    private fun applyKey(scale: Double, key: Int, keys: Keys) : Keys =
        when(key) {
            37 -> keys.copy(x = -scale)
            38 -> keys.copy(y = scale)
            39 -> keys.copy(x = scale)
            40 -> keys.copy(y = -scale)
            else -> keys
        }

    private fun walk(model : Model) : Model =
        model
            .copy(vx = model.keys.x)
            .copy(dir =
                when {
                    model.keys.x < 0 -> Direction.Left
                    model.keys.x > 0 -> Direction.Right
                    else -> model.dir
                }
        )

    private fun gravity(dt: Double, model: Model) : Model =
        model.copy(vy =
            if (model.y > 0)
                model.vy - dt / 4
            else
                0.0
        )

    private fun physics(dt: Double, model: Model) =
        model.copy( x = model.x + dt * model.vx,
                    y = max(0.0, (model.y + dt * model.vy)))

    private fun jump(model: Model) =
        if (model.keys.y > 0 && model.vy == 0.0)
            model.copy(vy = 6.0)
        else
            model
}