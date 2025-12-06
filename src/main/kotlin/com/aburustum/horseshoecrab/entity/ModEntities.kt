package com.aburustum.horseshoecrab.entity

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

class ModEntities {
    companion object {
        val HORSESHOE_CRAB: EntityType<HorseshoeCrabEntity> =
            Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(HorseshoeCrab.MOD_ID, "crab"),
                EntityType.Builder
                    .create({ type, world -> HorseshoeCrabEntity(type, world) }, SpawnGroup.WATER_AMBIENT)
                    .dimensions(0.5f, 0.3f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(HorseshoeCrab.MOD_ID, "crab"))),
            )

        fun registerModEntities() {
            HorseshoeCrab.LOGGER.info("Registering mod entities for ${HorseshoeCrab.LOGGER}")
        }
    }
}
