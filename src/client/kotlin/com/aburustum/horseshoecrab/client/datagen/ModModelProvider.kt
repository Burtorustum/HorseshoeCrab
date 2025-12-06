package com.aburustum.horseshoecrab.client.datagen

import com.aburustum.horseshoecrab.item.ModItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplates

class ModModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators) {
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
        itemModelGenerator.generateFlatItem(ModItems.BLUE_BLOOD_VIAL, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.HORSESHOE_CRAB_SPAWN_EGG, ModelTemplates.FLAT_ITEM)
    }
}
