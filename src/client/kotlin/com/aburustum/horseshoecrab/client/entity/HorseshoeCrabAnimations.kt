package com.aburustum.horseshoecrab.client.entity

import net.minecraft.client.render.entity.animation.AnimationDefinition
import net.minecraft.client.render.entity.animation.AnimationHelper
import net.minecraft.client.render.entity.animation.Keyframe
import net.minecraft.client.render.entity.animation.Transformation

class HorseshoeCrabAnimations {
    companion object {
        val ANIM_IDLE: AnimationDefinition =
            AnimationDefinition.Builder
                .create(4.0F)
                .looping()
                .addBoneAnimation(
                    "crab",
                    Transformation(
                        Transformation.Targets.ROTATE,
                        Keyframe(
                            0.0F,
                            AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0F,
                            AnimationHelper.createRotationalVector(0.0F, 20.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            3.0F,
                            AnimationHelper.createRotationalVector(0.0F, -20.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            4.0F,
                            AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                    ),
                ).build()

        val ANIM_WALK: AnimationDefinition =
            AnimationDefinition.Builder
                .create(2F)
                .looping()
                .addBoneAnimation(
                    "crab",
                    Transformation(
                        Transformation.Targets.ROTATE,
                        Keyframe(
                            0.0F,
                            AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0F,
                            AnimationHelper.createRotationalVector(0.0F, 0.0F, 10.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            2.0F,
                            AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                    ),
                ).addBoneAnimation(
                    "crab",
                    Transformation(
                        Transformation.Targets.MOVE_ORIGIN,
                        Keyframe(
                            0.0F,
                            AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0F,
                            AnimationHelper.createTranslationalVector(-1.0F, 0.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            2.0F,
                            AnimationHelper.createTranslationalVector(-3.0F, 0.0F, 0.0F),
                            Transformation.Interpolations.LINEAR,
                        ),
                    ),
                ).build()
    }
}
