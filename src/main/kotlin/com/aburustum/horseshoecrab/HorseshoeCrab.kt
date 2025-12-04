package com.aburustum.horseshoecrab

import com.aburustum.horseshoecrab.entity.ModEntities
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import com.aburustum.horseshoecrab.item.ModItemGroups
import com.aburustum.horseshoecrab.item.ModItems
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HorseshoeCrab : ModInitializer {
    companion object {
        const val MOD_ID = "horseshoecrab"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    }

    override fun onInitialize() {
        ModItemGroups.registerItemGroups()

        ModItems.registerModItems()

        ModEntities.registerModEntities()

        FabricDefaultAttributeRegistry.register(ModEntities.HORSESHOE_CRAB, HorseshoeCrabEntity.createAttributes())

        LOGGER.info("HorseshoeCrab initialized")
    }
}
