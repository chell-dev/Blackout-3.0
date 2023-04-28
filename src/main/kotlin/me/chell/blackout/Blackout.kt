package me.chell.blackout

import me.chell.blackout.api.event.EventManager
import me.chell.blackout.api.feature.FeatureManager
import me.chell.blackout.api.util.*
import me.chell.blackout.impl.gui.ClientGUI
import me.chell.blackout.impl.gui.HudEditor

class Blackout {
    companion object {
        lateinit var eventManager: EventManager
        lateinit var featureManager: FeatureManager
        lateinit var clientGUI: ClientGUI
        lateinit var hudEditor: HudEditor
    }

    fun init() {
        eventManager = EventManager()
        featureManager = FeatureManager()
        featureManager.init()

        val files = readClientFile()
        readFeatures(files[0])
        readFriends(files[1])

        clientGUI = ClientGUI()
        hudEditor = HudEditor()
        eventManager.register(Rainbow)

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Saving config")
            writeClientFile()
            val f = readClientFile()
            writeFeatures(f[0])
            writeFriends(f[1])
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