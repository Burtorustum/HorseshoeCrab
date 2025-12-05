package com.aburustum.horseshoecrab.client

import com.aburustum.horseshoecrab.client.datagen.ModModelProvider
import com.aburustum.horseshoecrab.world.ModConfiguredFeatures
import com.aburustum.horseshoecrab.world.ModPlacedFeatures
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.registry.RegistryBuilder
import net.minecraft.registry.RegistryKeys

class HorseshoeCrabDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()

        pack.addProvider { output: FabricDataOutput -> ModModelProvider(output) }
    }

    override fun buildRegistry(registryBuilder: RegistryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
    }
}
