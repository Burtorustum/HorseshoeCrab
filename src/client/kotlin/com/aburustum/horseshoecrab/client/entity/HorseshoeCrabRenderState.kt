package com.aburustum.horseshoecrab.client.entity

import com.aburustum.horseshoecrab.entity.custom.HorseshoeCrabVariant
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.world.entity.AnimationState

class HorseshoeCrabRenderState : LivingEntityRenderState() {
    val idleAnimationState: AnimationState = AnimationState()
    var variant: HorseshoeCrabVariant = HorseshoeCrabVariant.MALE
}
