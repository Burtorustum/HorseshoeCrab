package com.aburustum.horseshoecrab.world.gen

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.ModEntities
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.SpawnLocationTypes
import net.minecraft.entity.SpawnRestriction
import net.minecraft.world.Heightmap
import net.minecraft.world.biome.BiomeKeys

class ModEntitySpawns {
    companion object {
        fun addSpawns() {
            HorseshoeCrab.LOGGER.info("Adding entity spawns for ${HorseshoeCrab.MOD_ID}")

            BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                    BiomeKeys.BEACH,
                    BiomeKeys.OCEAN,
                    BiomeKeys.WARM_OCEAN,
                    BiomeKeys.LUKEWARM_OCEAN,
                    BiomeKeys.COLD_OCEAN,
                ),
                SpawnGroup.WATER_AMBIENT, // TODO: custom spawngroup
                ModEntities.HORSESHOE_CRAB,
                5,
                1,
                3,
            )
            SpawnRestriction.register(
                ModEntities.HORSESHOE_CRAB,
                SpawnLocationTypes.UNRESTRICTED,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                HorseshoeCrabEntity::canSpawn,
            )
        }
    }
}
