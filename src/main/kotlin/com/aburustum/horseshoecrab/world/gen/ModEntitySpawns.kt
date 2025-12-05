package com.aburustum.horseshoecrab.world.gen

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
            BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(BiomeKeys.BEACH),
                SpawnGroup.CREATURE,
                ModEntities.HORSESHOE_CRAB,
                2,
                1,
                3,
            )

            SpawnRestriction.register(
                ModEntities.HORSESHOE_CRAB,
                SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                HorseshoeCrabEntity::canSpawn,
            )
        }
    }
}
