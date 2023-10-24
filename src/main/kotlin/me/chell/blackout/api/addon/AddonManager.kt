package me.chell.blackout.api.addon

import me.chell.blackout.api.command.CommandManager
import me.chell.blackout.api.feature.Category
import me.chell.blackout.api.feature.FeatureManager
import java.io.File
import java.net.URLClassLoader
import java.util.zip.ZipFile

object AddonManager {

    val addons = mutableListOf<Addon>()

    //https://github.com/chell-dev/Samsara/blob/master/src/main/kotlin/me/chell/samsara/api/addon/AddonManager.kt
    init {
        val folder = File("mods/")

        for(f in folder.listFiles()!!) {
            if(f.name.endsWith(".jar", true)) {

                for(entry in ZipFile(f).entries()) {
                    if(entry.name.endsWith(".class")) {

                        val classLoader = URLClassLoader.newInstance(arrayOf(f.toURI().toURL()), javaClass.classLoader); // thank you seppuku
                        val clazz = classLoader.loadClass(entry.name.dropLast(".class".length).replace('/', '.'))

                        if(Addon::class.java.isAssignableFrom(clazz)) {
                            val a = clazz.getDeclaredConstructor().newInstance() as Addon
                            addons.add(a)
                            //LOG.info("Found addon in ${f.name}.")
                            break
                        }

                    }
                }

            }
        }
    }

    fun preInit() {
        addons.forEach{
            for(f in it.features) {
                f.category = Category.Addons
                FeatureManager.features.add(f)
            }

            for(c in it.commands) {
                CommandManager.commands.add(c)
            }
        }
    }

    fun postInit() = addons.forEach{it.init()}

    fun shutdown() = addons.forEach{it.shutdown()}

}