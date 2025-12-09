package com.aburustum.horseshoecrab.client.entity

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabVariant
import com.google.common.collect.Maps
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.Util
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.resources.ResourceLocation

class HorseshoeCrabRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<HorseshoeCrabEntity, HorseshoeCrabRenderState, HorseshoeCrabModel>(
        context,
        HorseshoeCrabModel(context.bakeLayer(HorseshoeCrabModel.HORSESHOE_CRAB)),
        1.0f,
    ) {

    companion object {
        val LOCATION_BY_VARIANT: Map<HorseshoeCrabVariant, ResourceLocation> =
            Util.make(Maps.newEnumMap(HorseshoeCrabVariant::class.java)) {
                it[HorseshoeCrabVariant.FEMALE] =
                    ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, "textures/entity/horseshoe_crab_female.png")
                it[HorseshoeCrabVariant.MALE] =
                    ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, "textures/entity/horseshoe_crab_male.png")
            }
    }

    override fun getTextureLocation(state: HorseshoeCrabRenderState): ResourceLocation = LOCATION_BY_VARIANT[state.variant]!!

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
        livingEntityRenderState.variant = livingEntity.getVariant()
    }
}
