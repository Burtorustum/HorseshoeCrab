package com.aburustum.horseshoecrab.client

import com.aburustum.horseshoecrab.client.datagen.ModModelProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput

class HorseshoeCrabDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()

        pack.addProvider { output: FabricDataOutput -> ModModelProvider(output) }
    }
}
