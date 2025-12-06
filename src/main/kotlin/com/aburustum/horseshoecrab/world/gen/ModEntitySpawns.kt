package com.aburustum.horseshoecrab.world.gen

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.ModEntities
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.world.entity.MobCategory
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
                    Biomes.OCEAN,
                    Biomes.WARM_OCEAN,
                    Biomes.LUKEWARM_OCEAN,
                    Biomes.COLD_OCEAN,
                ),
                MobCategory.WATER_AMBIENT, // TODO: custom spawngroup
                ModEntities.HORSESHOE_CRAB,
                10,
                1,
                4,
            )
            SpawnPlacements.register(
                ModEntities.HORSESHOE_CRAB,
                SpawnPlacementTypes.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                HorseshoeCrabEntity::canSpawn,
            )
        }
    }
}
