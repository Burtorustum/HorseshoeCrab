package com.aburustum.horseshoecrab.client.datagen

import com.aburustum.horseshoecrab.item.ModItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.Models

class ModModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
        itemModelGenerator.register(ModItems.BLUE_BLOOD_VIAL, Models.GENERATED)
        itemModelGenerator.register(ModItems.HORSESHOE_CRAB_SPAWN_EGG, Models.GENERATED)
    }
}
