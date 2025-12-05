package com.aburustum.horseshoecrab.client.entity

import net.minecraft.client.render.entity.animation.AnimationDefinition
import net.minecraft.client.render.entity.animation.AnimationHelper
import net.minecraft.client.render.entity.animation.Keyframe
import net.minecraft.client.render.entity.animation.Transformation

class HorseshoeCrabAnimations {
    companion object {
        val ANIM_IDLE: AnimationDefinition =
            AnimationDefinition.Builder
                .create(4.0f)
                .looping()
                .addBoneAnimation(
                    "crab",
                    Transformation(
                        Transformation.Targets.ROTATE,
                        Keyframe(
                            0.0f,
                            AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0f,
                            AnimationHelper.createRotationalVector(0.0f, -10.0f, 0.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            3.0f,
                            AnimationHelper.createRotationalVector(0.0f, 10.0f, 0.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            4.0f,
                            AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                    ),
                ).build()

        val ANIM_WALK: AnimationDefinition =
            AnimationDefinition.Builder
                .create(2.0f)
                .looping()
                .addBoneAnimation(
                    "crab",
                    Transformation(
                        Transformation.Targets.ROTATE,
                        Keyframe(
                            0.0f,
                            AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0f,
                            AnimationHelper.createRotationalVector(-90.0f, 12.5f, 90.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            2.0f,
                            AnimationHelper.createRotationalVector(0.0f, 0.0f, 0.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                    ),
                ).addBoneAnimation(
                    "crab",
                    Transformation(
                        Transformation.Targets.MOVE_ORIGIN,
                        Keyframe(
                            0.0f,
                            AnimationHelper.createTranslationalVector(0.0f, 0.0f, 0.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            1.0f,
                            AnimationHelper.createTranslationalVector(0.0f, 0.0f, -1.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                        Keyframe(
                            2.0f,
                            AnimationHelper.createTranslationalVector(0.0f, 0.0f, -3.0f),
                            Transformation.Interpolations.LINEAR,
                        ),
                    ),
                ).build()
    }
}
