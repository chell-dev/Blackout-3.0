package me.chell.blackout.impl.gui.old

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

class ModMenu: ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory { ClientGUI }
}