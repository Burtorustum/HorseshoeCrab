package com.aburustum.horseshoecrab.client

import com.aburustum.horseshoecrab.client.entity.HorseshoeCrabModel
import com.aburustum.horseshoecrab.client.entity.HorseshoeCrabRenderer
import com.aburustum.horseshoecrab.entity.ModEntities
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.minecraft.client.renderer.entity.EntityRenderers

class HorseshoeCrabClient : ClientModInitializer {
    override fun onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(
            HorseshoeCrabModel.HORSESHOE_CRAB,
            HorseshoeCrabModel::getTexturedModelData,
        )
        EntityRenderers.register(ModEntities.HORSESHOE_CRAB, ::HorseshoeCrabRenderer)
    }
}
