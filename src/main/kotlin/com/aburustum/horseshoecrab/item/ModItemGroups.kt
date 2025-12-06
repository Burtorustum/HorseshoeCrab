package com.aburustum.horseshoecrab.item

import com.aburustum.horseshoecrab.HorseshoeCrab
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

class ModItemGroups {
    companion object {
        val HORSESHOE_CRAB_ITEMS_GROUP: CreativeModeTab =
            Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, "horseshoecrab_items"),
                FabricItemGroup
                    .builder()
                    .icon { ItemStack(ModItems.BLUE_BLOOD_VIAL) }
                    .title(Component.translatable("itemgroup.horseshoecrab.horseshoecrab_items"))
                    .displayItems { displayContext, entries ->
                        run {
                            entries.accept { ModItems.BLUE_BLOOD_VIAL }
                            entries.accept { ModItems.HORSESHOE_CRAB_SPAWN_EGG }
                        }
                    }.build(),
            )

        fun registerItemGroups() {
            HorseshoeCrab.LOGGER.info("Registering item groups for ${HorseshoeCrab.MOD_ID}")
        }
    }
}
