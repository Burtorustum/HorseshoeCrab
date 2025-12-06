package com.aburustum.horseshoecrab.item

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.ModEntities
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.SpawnEggItem
import java.util.function.Function

class ModItems {
    companion object {
        val BLUE_BLOOD_VIAL = registerItem("blue_blood_vial", ::Item)

        val HORSESHOE_CRAB_SPAWN_EGG =
            registerItem("horseshoe_crab_spawn_egg") { setting -> SpawnEggItem(setting.spawnEgg(ModEntities.HORSESHOE_CRAB)) }

        private fun registerItem(name: String, function: Function<Item.Properties, Item>): Item = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, name),
            function.apply(
                Item
                    .Properties()
                    .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, name))),
            ),
        )

        fun registerModItems() {
            HorseshoeCrab.LOGGER.info("Registering items for ${HorseshoeCrab.MOD_ID}")

            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register { entries ->
                entries.accept(BLUE_BLOOD_VIAL)
            }

            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register { entries ->
                entries.accept(HORSESHOE_CRAB_SPAWN_EGG)
            }
        }
    }
}
