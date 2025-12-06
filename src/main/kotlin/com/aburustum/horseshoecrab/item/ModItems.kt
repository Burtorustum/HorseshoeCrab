package com.aburustum.horseshoecrab.item

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.ModEntities
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.SpawnEggItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import java.util.function.Function

class ModItems {
    companion object {
        val BLUE_BLOOD_VIAL = registerItem("blue_blood_vial", ::Item)

        val HORSESHOE_CRAB_SPAWN_EGG =
            registerItem("horseshoe_crab_spawn_egg") { setting -> SpawnEggItem(setting.spawnEgg(ModEntities.HORSESHOE_CRAB)) }

        private fun registerItem(name: String, function: Function<Item.Settings, Item>): Item = Registry.register(
            Registries.ITEM,
            Identifier.of(HorseshoeCrab.MOD_ID, name),
            function.apply(
                Item
                    .Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(HorseshoeCrab.MOD_ID, name))),
            ),
        )

        fun registerModItems() {
            HorseshoeCrab.LOGGER.info("Registering items for ${HorseshoeCrab.MOD_ID}")

            ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register { entries ->
                entries.add(BLUE_BLOOD_VIAL)
            }

            ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register { entries ->
                entries.add(HORSESHOE_CRAB_SPAWN_EGG)
            }
        }
    }
}
