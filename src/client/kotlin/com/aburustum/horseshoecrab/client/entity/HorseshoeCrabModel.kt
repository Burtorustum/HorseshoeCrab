package com.aburustum.horseshoecrab.client.entity

import com.aburustum.horseshoecrab.HorseshoeCrab
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.animation.Animation
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.Identifier

class HorseshoeCrabModel(root: ModelPart) : EntityModel<HorseshoeCrabRenderState>(root) {
    private val crab: ModelPart = root.getChild("crab")

    private val walkingAnimation: Animation = HorseshoeCrabAnimations.ANIM_WALK.createAnimation(root)
    private val idleAnimation: Animation = HorseshoeCrabAnimations.ANIM_IDLE.createAnimation(root)

    override fun setAngles(state: HorseshoeCrabRenderState) {
        super.setAngles(state)
        this.walkingAnimation.applyWalking(state.limbSwingAnimationProgress, state.limbSwingAmplitude, 1f, 1.5f)
        this.idleAnimation.apply(state.idleAnimationState, state.age, 1f)
    }

    companion object {
        val HORSESHOE_CRAB: EntityModelLayer = EntityModelLayer(Identifier.of(HorseshoeCrab.MOD_ID, "crab"), "main")

        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            modelPartData.addChild(
                "crab",
                ModelPartBuilder
                    .create()
                    .uv(0, 0)
                    .cuboid(-4.0f, -2.0f, -3.0f, 8.0f, 2.0f, 7.0f, Dilation(0.0f))
                    .uv(0, 9)
                    .cuboid(-3.0f, -3.0f, -2.0f, 6.0f, 1.0f, 5.0f, Dilation(0.0f))
                    .uv(0, 15)
                    .cuboid(4.0f, -1.0f, 0.0f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.origin(0.0f, 24.0f, 0.0f),
            )
            return TexturedModelData.of(modelData, 32, 32)
        }
    }
}
