package com.aburustum.horseshoecrab.item

import com.aburustum.horseshoecrab.HorseshoeCrab
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class ModItemGroups {
    companion object {
        val HORSESHOE_CRAB_ITEMS_GROUP: ItemGroup =
            Registry.register(
                Registries.ITEM_GROUP,
                Identifier.of(HorseshoeCrab.MOD_ID, "horseshoecrab_items"),
                FabricItemGroup
                    .builder()
                    .icon { ItemStack(ModItems.BLUE_BLOOD_VIAL) }
                    .displayName(Text.translatable("itemgroup.horseshoecrab.horseshoecrab_items"))
                    .entries { displayContext, entries ->
                        run {
                            entries.add { ModItems.BLUE_BLOOD_VIAL }
                            entries.add { ModItems.HORSESHOE_CRAB_SPAWN_EGG }
                        }
                    }.build(),
            )

        fun registerItemGroups() {
            HorseshoeCrab.LOGGER.info("Registering item groups for ${HorseshoeCrab.MOD_ID}")
        }
    }
}
