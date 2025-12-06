package com.aburustum.horseshoecrab.client.entity

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.resources.ResourceLocation

class HorseshoeCrabRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<HorseshoeCrabEntity, HorseshoeCrabRenderState, HorseshoeCrabModel>(
        context,
        HorseshoeCrabModel(context.bakeLayer(HorseshoeCrabModel.HORSESHOE_CRAB)),
        0.75f,
    ) {
    override fun getTextureLocation(state: HorseshoeCrabRenderState): ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, "textures/entity/crab.png")

    override fun submit(
        state: HorseshoeCrabRenderState,
        matrixStack: PoseStack,
        orderedRenderCommandQueue: SubmitNodeCollector,
        cameraRenderState: CameraRenderState,
    ) {
        if (state.isBaby) {
            matrixStack.scale(0.5f, 0.5f, 0.5f)
        } else {
            matrixStack.scale(1.0f, 1.0f, 1.0f)
        }

        super.submit(state, matrixStack, orderedRenderCommandQueue, cameraRenderState)
    }

    override fun createRenderState(): HorseshoeCrabRenderState = HorseshoeCrabRenderState()

    override fun extractRenderState(livingEntity: HorseshoeCrabEntity, livingEntityRenderState: HorseshoeCrabRenderState, f: Float) {
        super.extractRenderState(livingEntity, livingEntityRenderState, f)
        livingEntityRenderState.idleAnimationState.copyFrom(livingEntity.idleAnimationState)
    }
}
