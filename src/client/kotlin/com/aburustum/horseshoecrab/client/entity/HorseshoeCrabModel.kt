package com.aburustum.horseshoecrab.client.entity

import com.aburustum.horseshoecrab.HorseshoeCrab
import net.minecraft.client.animation.KeyframeAnimation
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation

class HorseshoeCrabModel(root: ModelPart) : EntityModel<HorseshoeCrabRenderState>(root) {
    private val crab: ModelPart = root.getChild("crab")

    private val walkingAnimation: KeyframeAnimation = HorseshoeCrabAnimations.ANIM_WALK.bake(root)
    private val idleAnimation: KeyframeAnimation = HorseshoeCrabAnimations.ANIM_IDLE.bake(root)

    override fun setupAnim(state: HorseshoeCrabRenderState) {
        super.setupAnim(state)
        this.walkingAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1f, 1.5f)
        this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks, 1f)
    }

    companion object {
        val HORSESHOE_CRAB: ModelLayerLocation =
            ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(HorseshoeCrab.MOD_ID, "crab"), "main")

        fun getTexturedModelData(): LayerDefinition {
            val modelData = MeshDefinition()
            val modelPartData = modelData.root
            modelPartData.addOrReplaceChild(
                "crab",
                CubeListBuilder
                    .create()
                    .texOffs(0, 0)
                    .addBox(-4.0f, -2.0f, -3.0f, 8.0f, 2.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(0, 9)
                    .addBox(-3.0f, -3.0f, -2.0f, 6.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 15)
                    .addBox(4.0f, -1.0f, 0.0f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f),
            )
            return LayerDefinition.create(modelData, 32, 32)
        }
    }
}
