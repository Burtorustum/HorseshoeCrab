package com.aburustum.horseshoecrab.world

import com.aburustum.horseshoecrab.HorseshoeCrab
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration

class ModConfiguredFeatures {
    companion object {
        fun bootstrap(context: BootstrapContext<ConfiguredFeature<*, *>>) {}

        fun registerKey(name: String): ResourceKey<ConfiguredFeature<*, *>> =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, name))

        private fun <FC : FeatureConfiguration, F : Feature<FC>> register(
            context: BootstrapContext<ConfiguredFeature<*, *>>,
            key: ResourceKey<ConfiguredFeature<*, *>>,
            feature: F,
            configuration: FC,
        ) {
            context.register(key, ConfiguredFeature<FC, F>(feature, configuration))
        }
    }
}
