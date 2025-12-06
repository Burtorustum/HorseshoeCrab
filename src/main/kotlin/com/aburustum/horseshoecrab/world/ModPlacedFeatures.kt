package com.aburustum.horseshoecrab.world

import com.aburustum.horseshoecrab.HorseshoeCrab
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import java.util.List

class ModPlacedFeatures {
    companion object {
        fun bootstrap(context: BootstrapContext<PlacedFeature>) {
            val configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE)
        }

        fun registerKey(name: String): ResourceKey<PlacedFeature> =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, name))

        private fun register(
            context: BootstrapContext<PlacedFeature>,
            key: ResourceKey<PlacedFeature>,
            configuration: Holder<ConfiguredFeature<*, *>>,
            modifiers: MutableList<PlacementModifier>,
        ) {
            context.register(key, PlacedFeature(configuration, List.copyOf<PlacementModifier>(modifiers)))
        }

        private fun <FC : FeatureConfiguration, F : Feature<FC>> register(
            context: BootstrapContext<PlacedFeature>,
            key: ResourceKey<PlacedFeature>,
            configuration: Holder<ConfiguredFeature<*, *>>,
            vararg modifiers: PlacementModifier,
        ) {
            register(context, key, configuration, mutableListOf(*modifiers))
        }
    }
}
