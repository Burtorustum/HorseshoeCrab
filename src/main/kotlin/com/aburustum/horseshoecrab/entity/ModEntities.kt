package com.aburustum.horseshoecrab.entity

import com.aburustum.horseshoecrab.CustomMobCategories
import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType

class ModEntities {
    companion object {
        val HORSESHOE_CRAB: EntityType<HorseshoeCrabEntity> =
            Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, "crab"),
                EntityType.Builder
                    .of(::HorseshoeCrabEntity, CustomMobCategories.HORSESHOE_CRAB.mobCategory)
                    .sized(0.5f, 0.3f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, "crab"))),
            )

        fun registerModEntities() {
            HorseshoeCrab.LOGGER.info("Registering mod entities for ${HorseshoeCrab.LOGGER}")
        }
    }
}
