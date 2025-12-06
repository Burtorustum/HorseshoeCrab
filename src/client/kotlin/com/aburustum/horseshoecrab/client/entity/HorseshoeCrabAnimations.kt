package com.aburustum.horseshoecrab.client.entity

import net.minecraft.client.animation.AnimationChannel
import net.minecraft.client.animation.AnimationDefinition
import net.minecraft.client.animation.Keyframe
import net.minecraft.client.animation.KeyframeAnimations

class HorseshoeCrabAnimations {
    companion object {
        val ANIM_IDLE: AnimationDefinition =
            AnimationDefinition.Builder
                .withLength(4.0f)
                .looping()
                .addAnimation(
                    "crab",
                    AnimationChannel(
                        AnimationChannel.Targets.ROTATION,
                        Keyframe(
                            0.0f,
                            KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0f,
                            KeyframeAnimations.degreeVec(0.0f, -10.0f, 0.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            3.0f,
                            KeyframeAnimations.degreeVec(0.0f, 10.0f, 0.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            4.0f,
                            KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                    ),
                ).build()

        val ANIM_WALK: AnimationDefinition =
            AnimationDefinition.Builder
                .withLength(2.0f)
                .looping()
                .addAnimation(
                    "crab",
                    AnimationChannel(
                        AnimationChannel.Targets.ROTATION,
                        Keyframe(
                            0.0f,
                            KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0f,
                            KeyframeAnimations.degreeVec(-90.0f, 12.5f, 90.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            2.0f,
                            KeyframeAnimations.degreeVec(0.0f, 0.0f, 0.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                    ),
                ).addAnimation(
                    "crab",
                    AnimationChannel(
                        AnimationChannel.Targets.POSITION,
                        Keyframe(
                            0.0f,
                            KeyframeAnimations.posVec(0.0f, 0.0f, 0.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0f,
                            KeyframeAnimations.posVec(0.0f, 0.0f, -1.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            2.0f,
                            KeyframeAnimations.posVec(0.0f, 0.0f, -3.0f),
                            AnimationChannel.Interpolations.LINEAR,
                        ),
                    ),
                ).build()
    }
}
