package me.chell.blackout.api.feature

import org.reflections.Reflections
import org.reflections.scanners.Scanners

class FeatureManager {

    val features = mutableListOf<Feature>()

    init {
        registerFeatures()
    }

    private fun registerFeatures() {
        val list = Reflections("me.chell.blackout.impl.features").get(Scanners.SubTypes.of(Feature::class.java).asClass<Feature>())

        for(c in list) {
            if(!c.isAnnotationPresent(NoRegister::class.java))
                features.add(c.getDeclaredConstructor().newInstance() as Feature)
        }
    }

}