package me.wieku.framework.input

import me.wieku.framework.input.event.*
import org.joml.Vector2f
import org.joml.Vector2i
import java.util.*
import kotlin.collections.ArrayList

abstract class InputManager {

    private val pos = Vector2i()
    private val posF = Vector2f()

    var inputHandler: InputHandler? = null

    var inputQueue = ArrayDeque<InputHandler>()
    var inputQueue1 = ArrayDeque<InputHandler>()

    fun getPosition() = pos

    fun getPositionF() = posF

    fun updatePosition(x: Float, y: Float, cursorInWindow: Boolean = true) {
        posF.set(x, y)
        pos.set(x.toInt(), y.toInt())
        updateHover(cursorInWindow)
    }

    private var toRelease = ArrayList<InputHandler>()
    private var pressed = ArrayList<InputHandler>()
    private var lastPressHolder: InputHandler? = null
    private var pressHolder: InputHandler? = null

    fun updateCursorAction(mouseButton: MouseButton, inputAction: InputAction) {

        inputHandler?.let { inputHandler ->
            lastPressHolder = pressHolder
            pressHolder = null
            inputQueue.clear()
            inputHandler.buildInputQueue(pos, inputQueue)

            var wasClicked = false

            run loop@{
                inputQueue.forEach { handler ->

                    if (inputAction == InputAction.Press) {
                        pressed.add(handler)
                    } else if (inputAction == InputAction.Release) {
                        pressed.remove(handler)
                        handler.trigger(MouseUpEvent(pos, mouseButton))

                        if (!wasClicked && mouseButton == MouseButton.ButtonLeft) {
                            wasClicked = handler.trigger(ClickEvent(pos))
                        }
                    }

                    if (handler === lastPressHolder) {
                        pressHolder = lastPressHolder
                        return@loop
                    }

                    if (inputAction == InputAction.Press && handler.trigger(MouseDownEvent(pos, mouseButton))) {
                        pressHolder = handler
                        return@loop
                    }

                }
            }

            if (inputAction == InputAction.Release) {
                pressed.forEach { handler ->
                    handler.trigger(MouseUpEvent(pos, mouseButton))
                }
                pressed.clear()
                pressHolder = null
            }
        }
    }

    var lastHovered = ArrayList<InputHandler>()
    var hovered = ArrayList<InputHandler>()
    var lastHoverHolder: InputHandler? = null
    var hoverHolder: InputHandler? = null

    private fun updateHover(isInWindow: Boolean = true) {
        //println("hover update")
        inputHandler?.let { inputHandler ->
            lastHoverHolder = hoverHolder
            hoverHolder = null
            inputQueue.clear()
            inputHandler.buildInputQueue(pos, inputQueue)

            lastHovered.clear()
            lastHovered.addAll(hovered)
            hovered.clear()

            if (isInWindow) {
                run loop@{
                    inputQueue.forEach { handler ->
                        hovered.add(handler)
                        lastHovered.remove(handler)

                        if (handler === lastHoverHolder) {
                            hoverHolder = lastHoverHolder
                            return@loop
                        }

                        if (!handler.isHovered) {
                            handler.isHovered = true
                            if (handler.trigger(HoverEvent(pos))) {
                                hoverHolder = handler
                                return@loop
                            }

                        }
                    }
                }
            }

            lastHovered.forEach { handler ->
                handler.isHovered = false
                handler.trigger(HoverLostEvent(pos))
            }

        }
    }

    protected fun updateScroll(xOffset: Float, yOffset: Float) {
        val offset = Vector2f(xOffset, yOffset)

        inputHandler?.let { inputHandler ->

            inputQueue1.clear()
            inputHandler.buildInputQueue(pos, inputQueue1)

            run loop@{
                inputQueue1.forEach { handler ->
                    if (handler.trigger(ScrollEvent(pos, offset))) {
                        hoverHolder = handler
                        return@loop
                    }
                }
            }
        }
    }

    abstract fun update()

}