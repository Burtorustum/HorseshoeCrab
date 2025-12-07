package com.aburustum.horseshoecrab.world.gen

import com.aburustum.horseshoecrab.CustomMobCategories
import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.ModEntities
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.world.entity.SpawnPlacementTypes
import net.minecraft.world.entity.SpawnPlacements
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.levelgen.Heightmap

class ModEntitySpawns {
    companion object {
        fun addSpawns() {
            HorseshoeCrab.LOGGER.info("Adding entity spawns for ${HorseshoeCrab.MOD_ID}")

            BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                    Biomes.BEACH,
                ),
                CustomMobCategories.HORSESHOE_CRAB.mobCategory,
                ModEntities.HORSESHOE_CRAB,
                20,
                2,
                8,
            )
            SpawnPlacements.register(
                ModEntities.HORSESHOE_CRAB,
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                HorseshoeCrabEntity::canSpawn,
            )
        }
    }
}
