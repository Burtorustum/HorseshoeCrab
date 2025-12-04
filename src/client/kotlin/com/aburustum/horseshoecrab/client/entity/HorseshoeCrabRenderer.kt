package com.aburustum.horseshoecrab.client.entity

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabEntity
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.state.CameraRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class HorseshoeCrabRenderer(
    context: EntityRendererFactory.Context,
) : MobEntityRenderer<HorseshoeCrabEntity, HorseshoeCrabRenderState, HorseshoeCrabModel>(
        context,
        HorseshoeCrabModel(context.getPart(HorseshoeCrabModel.HORSESHOE_CRAB)),
        0.75f,
    ) {
    override fun getTexture(state: HorseshoeCrabRenderState): Identifier = Identifier.of(HorseshoeCrab.MOD_ID, "textures/entity/crab.png")

    override fun render(
        state: HorseshoeCrabRenderState,
        matrixStack: MatrixStack,
        orderedRenderCommandQueue: OrderedRenderCommandQueue,
        cameraRenderState: CameraRenderState,
    ) {
        if (state.baby) {
            matrixStack.scale(0.5f, 0.5f, 0.5f)
        } else {
            matrixStack.scale(1.0f, 1.0f, 1.0f)
        }

        super.render(state, matrixStack, orderedRenderCommandQueue, cameraRenderState)
    }

    override fun createRenderState(): HorseshoeCrabRenderState = HorseshoeCrabRenderState()

    override fun updateRenderState(
        livingEntity: HorseshoeCrabEntity,
        livingEntityRenderState: HorseshoeCrabRenderState,
        f: Float,
    ) {
        super.updateRenderState(livingEntity, livingEntityRenderState, f)
        livingEntityRenderState.idleAnimationState.copyFrom(livingEntity.idleAnimationState)
    }
}
