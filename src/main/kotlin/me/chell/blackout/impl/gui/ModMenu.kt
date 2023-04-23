package me.chell.blackout.impl.gui

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.chell.blackout.Blackout

class ModMenu : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory { Blackout.clientGUI }
}