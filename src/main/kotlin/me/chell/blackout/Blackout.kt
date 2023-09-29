package me.chell.blackout

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.api.util.*
import me.chell.blackout.impl.gui.ClientGUI
import me.chell.blackout.impl.gui.HudEditor

object Blackout {

    fun init() {
        FeatureManager.init()

        val files = readClientFile()
        readFeatures(files[0])
        readFriends(files[1])
        readKD()

        ClientGUI.clientInit()
        HudEditor.clientInit()
        EventManager.register(Rainbow)
        EventManager.register(CombatTracker)

        Runtime.getRuntime().addShutdownHook(Thread{
            println("Saving config")
            writeClientFile()
            val f = readClientFile()
            writeFeatures(f[0])
            writeFriends(f[1])
            writeKD()
        })

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
            
            
            """.trimIndent())
    }
}