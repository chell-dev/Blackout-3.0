package me.chell.blackout.impl.gui

import me.chell.blackout.api.command.CommandManager
import me.chell.blackout.api.util.*
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import org.lwjgl.glfw.GLFW
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import kotlin.math.max
import kotlin.math.min

object Console: DrawableHelper() {

    private var x = 350
    private var y = 50
    private var width = 500
    private var height = 300
    private var border = 1
    private var padding = 10

    private var isOpen = false
    private var scroll = 0

    private var listening = false
    private var grabbed = false
    private var grabX = 0
    private var grabY = 0

    private var input = ""
    //var selection = emptyArray<Int>()
    private val history = mutableListOf<String>()

    private var historySelected = -1
    private var cachedInput = ""

    private var ticks = 0

    val lines = mutableListOf<String>()

    private val background = Color(80, 80, 80, 240)
    private val borderColor = Color(64, 64, 64)

    private const val titleHeight = 20
    //const val resizeRadius = 2
    private const val inputHeight = 20

    init {
        print("${Formatting.GREEN}</blackout>${Formatting.DARK_GREEN} Welcome, ${mc.session.username}!")
        CommandManager.onCommand("help")
    }

    fun print(message: String) {
        lines.add(message)
    }

    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if(grabbed) {
            x = mouseX - grabX
            y = mouseY - grabY

            if(x < 300) x = 300
            if(y < 0) y = 0
            if(x + width > mc.window.scaledWidth) x = mc.window.scaledWidth - width
        }

        if(y + (if(isOpen) height else titleHeight) > mc.window.scaledHeight) y = mc.window.scaledHeight - (if(isOpen) height else titleHeight)

        fill(matrices, x+border, y, x+width-border, y+titleHeight, borderColor.rgb)
        textRenderer.draw(matrices, Formatting.GREEN + "Console", x + border + padding.toFloat(), y + (titleHeight / 2f) - (textRenderer.fontHeight / 2f), -1)

        if(!isOpen) return

        // border
        fill(matrices, x, y, x+border, y+height, borderColor.rgb)
        fill(matrices, x+width-border, y, x+width, y+height, borderColor.rgb)
        fill(matrices, x+border, y+height-border, x+width-border, y+height, borderColor.rgb)

        // background
        fill(matrices, x+border, y+titleHeight, x+width-border, y+height-border, background.rgb)

        enableScissor(x + border, y + titleHeight, x + width - border, y + height - border - inputHeight - padding)

        // lines
        var lineY = y + height - border - inputHeight - padding.toFloat() - (lines.size * textRenderer.fontHeight) + scroll

        for(string in lines) {
            textRenderer.draw(matrices, string, x + border + padding.toFloat(), lineY, -1)
            lineY += textRenderer.fontHeight
        }

        disableScissor()

        // input box
        fill(matrices, x + border, y + height - border - inputHeight, x + width - border, y + height - inputHeight, borderColor.rgb)

        var renderInput = input

        if(listening) {
            if(ticks < 10) renderInput += "_"
        } else {
            renderInput = Formatting.DARK_GREEN + "Click here to type."
        }

        textRenderer.draw(matrices, renderInput, x + border + padding.toFloat(), y + height - (inputHeight / 2f) - (textRenderer.fontHeight / 2f), -1)
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        listening = false
        if(mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + titleHeight) {
            if(button == 0) {
                grabX = mouseX.toInt() - x
                grabY = mouseY.toInt() - y
                grabbed = true
            } else {
                isOpen = !isOpen
            }
            return true
        } else if(isOpen && mouseX >= x + border && mouseY >= y + height - border - inputHeight && mouseX <= x + width - border && mouseY <= y + height - border) {
            ticks = 0
            listening = true
            return true
        }
        return false
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        grabbed = false
        return false
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if(isOpen && listening) {
            when(keyCode) {
                GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                    if(input.isNotEmpty()) {
                        print(Formatting.GREEN + SimpleDateFormat("HH:mm:ss >").format(Date.from(Instant.now())) + Formatting.RESET + input)
                        history.add(input)
                        CommandManager.onCommand(input)
                        input = ""
                        cachedInput = ""
                        historySelected = -1
                        scroll = 0
                        return true
                    }
                }
                GLFW.GLFW_KEY_BACKSPACE -> {
                    if(input.isNotEmpty()) {
                        input = input.dropLast(1)
                        return true
                    }
                }
                GLFW.GLFW_KEY_V -> {
                    if(modifiers == GLFW.GLFW_MOD_CONTROL) {
                        input += GLFW.glfwGetClipboardString(mc.window.handle)
                        return true
                    }
                }
                GLFW.GLFW_KEY_UP -> {
                    if(history.isEmpty()) return true
                    if(historySelected == -1) {
                        cachedInput = input
                        historySelected = history.size - 1
                    } else {
                        if(historySelected >= 1) historySelected--
                    }
                    input = history[historySelected]
                }
                GLFW.GLFW_KEY_DOWN -> {
                    if(history.isEmpty() || historySelected == -1) return true
                    if(historySelected == history.size - 1) {
                        input = cachedInput
                        historySelected = -1
                    } else {
                        historySelected++
                        input = history[historySelected]
                    }
                }
            }
        }

        return false
    }

    fun charTyped(chr: Char, modifiers: Int): Boolean {
        if(isOpen && listening) {
            input += chr
            return true
        }

        return false
    }

    fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if(isOpen && mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height) {
            val a = scroll + (amount * textRenderer.fontHeight).toInt()
            scroll = min(max(0, a), max((lines.size * textRenderer.fontHeight) - (height - titleHeight - border - inputHeight - padding - padding), 0))
            return true
        }
        return false
    }

    fun tick() {
        if(ticks >= 20) ticks = 0
        else ticks++
    }

}