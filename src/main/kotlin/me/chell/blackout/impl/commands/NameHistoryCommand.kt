package me.chell.blackout.impl.commands

import com.google.gson.JsonArray
import me.chell.blackout.api.command.Command
import me.chell.blackout.api.util.plus
import me.chell.blackout.impl.gui.Console
import net.minecraft.util.Formatting
import net.minecraft.util.JsonHelper
import java.net.URL

object NameHistoryCommand: Command("names", description = "Try to get a player's username history.") {

    override fun run(args: String) {

        if(args.length !in 3..16) {
            Console.print(Formatting.RED + "Invalid username.")
            return
        }

        val history: JsonArray

        try {
            val uuidJson = JsonHelper.deserialize(URL("https://api.mojang.com/users/profiles/minecraft/${args}").readText())
            val uuid = formatUuid(uuidJson.get("id").asString)
            history = JsonHelper.deserializeArray(URL("https://laby.net/api/user/${uuid}/get-names").readText())
        } catch (e: Exception) {
            //e.printStackTrace()
            Console.print(Formatting.RED + "Failed :(")
            return
        }

        if(history.size() > 1) {
            val last = history.size()-1
            Console.print("${Formatting.GRAY}${last}. ${Formatting.RESET}${history[last].asJsonObject.get("username").asString} ${Formatting.GRAY}(Current)")
            for (i in history.size() - 2 downTo 0) {
                Console.print("${Formatting.GRAY}${i + 1}. ${Formatting.RESET}${history[i].asJsonObject.get("username").asString}")
            }
        } else {
            Console.print(Formatting.RED+"No past usernames found.")
        }
    }

    private fun formatUuid(input: String): String {
        val output = StringBuilder()

        output.append(input.subSequence(0, 8))
        output.append("-")
        output.append(input.subSequence(8, 12))
        output.append("-")
        output.append(input.subSequence(12, 16))
        output.append("-")
        output.append(input.subSequence(16, 20))
        output.append("-")
        output.append(input.subSequence(20, 32))

        return output.toString()
    }
}
