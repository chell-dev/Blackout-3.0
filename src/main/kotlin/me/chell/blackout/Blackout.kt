package me.chell.blackout

import me.chell.blackout.api.addon.AddonManager
import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.api.util.*
import me.chell.blackout.impl.gui.old.ClientGUI
import me.chell.blackout.impl.gui.old.HudEditor

object Blackout {

    var initialized = false

    fun init() {
        AddonManager.preInit()
        FeatureManager.init()

        //BaritoneAPI.getProvider().worldScanner.scanChunkRadius()

        readConfig()

        ClientGUI.clientInit()
        HudEditor.clientInit()
        EventManager.register(Rainbow)
        EventManager.register(CombatTracker)
        Updater.checkUpdates()

        AddonManager.postInit()

        Runtime.getRuntime().addShutdownHook(Thread{
            println("[$modName] Saving config...")
            AddonManager.shutdown()
            writeConfig()
            println("[$modName] Goodbye.")
        })

        initialized = true

        print("""
            
             ▄▄▄▄    ██▓    ▄▄▄       ▄████▄   ██ ▄█▀ ▒█████   █    ██ ▄▄▄█████▓
            ▓█████▄ ▓██▒   ▒████▄    ▒██▀ ▀█   ██▄█▒ ▒██▒  ██▒ ██  ▓██▒▓  ██▒ ▓▒
            ▒██▒ ▄██▒██░   ▒██  ▀█▄  ▒▓█    ▄ ▓███▄░ ▒██░  ██▒▓██  ▒██░▒ ▓██░ ▒░
            ▒██░█▀  ▒██░   ░██▄▄▄▄██ ▒▓▓▄ ▄██▒▓██ █▄ ▒██   ██░▓▓█  ░██░░ ▓██▓ ░ 
            ░▓█  ▀█▓░██████▒▓█   ▓██▒▒ ▓███▀ ░▒██▒ █▄░ ████▓▒░▒▒█████▓   ▒██▒ ░ 
            ░▒▓███▀▒░ ▒░▓  ░▒▒   ▓▒█░░ ░▒ ▒  ░▒ ▒▒ ▓▒░ ▒░▒░▒░ ░▒▓▒ ▒ ▒   ▒ ░░   
            ▒░▒   ░ ░ ░ ▒  ░ ▒   ▒▒ ░  ░  ▒   ░ ░▒ ▒░  ░ ▒ ▒░ ░░▒░ ░ ░     ░    
             ░    ░   ░ ░    ░   ▒   ░        ░ ░░ ░ ░ ░ ░ ▒   ░░░ ░ ░   ░      
             ░          ░  ░     ░  ░░ ░      ░  ░       ░ ░     ░              
                  ░                  ░                                          
            
            made by chell with love, pain and suffering
            
            """.trimIndent())
    }
}