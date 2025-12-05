package com.aburustum.horseshoecrab.world

import com.aburustum.horseshoecrab.HorseshoeCrab
import net.minecraft.registry.Registerable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig
import net.minecraft.world.gen.feature.PlacedFeature
import net.minecraft.world.gen.placementmodifier.PlacementModifier
import java.util.List

class ModPlacedFeatures {
    companion object {
        fun bootstrap(context: Registerable<PlacedFeature>) {
            val configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE)
        }

        fun registerKey(name: String): RegistryKey<PlacedFeature> =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(HorseshoeCrab.MOD_ID, name))

        private fun register(
            context: Registerable<PlacedFeature>,
            key: RegistryKey<PlacedFeature>,
            configuration: RegistryEntry<ConfiguredFeature<*, *>>,
            modifiers: MutableList<PlacementModifier>,
        ) {
            context.register(key, PlacedFeature(configuration, List.copyOf<PlacementModifier>(modifiers)))
        }

        private fun <FC : FeatureConfig, F : Feature<FC>> register(
            context: Registerable<PlacedFeature>,
            key: RegistryKey<PlacedFeature>,
            configuration: RegistryEntry<ConfiguredFeature<*, *>>,
            vararg modifiers: PlacementModifier,
        ) {
            register(context, key, configuration, mutableListOf(*modifiers))
        }
    }
}
