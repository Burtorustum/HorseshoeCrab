package com.aburustum.horseshoecrab.client

import com.aburustum.horseshoecrab.client.datagen.ModModelProvider
import com.aburustum.horseshoecrab.world.ModConfiguredFeatures
import com.aburustum.horseshoecrab.world.ModPlacedFeatures
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries

class HorseshoeCrabDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()

        pack.addProvider(::ModModelProvider)
    }

    override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
        registryBuilder.add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
    }
}
