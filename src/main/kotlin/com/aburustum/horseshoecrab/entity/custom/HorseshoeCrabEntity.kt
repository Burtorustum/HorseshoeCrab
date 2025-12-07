package com.aburustum.horseshoecrab.entity.custom

import com.aburustum.horseshoecrab.HorseshoeCrab
import com.aburustum.horseshoecrab.entity.ModEntities
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.BreedGoal
import net.minecraft.world.entity.ai.goal.FollowParentGoal
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal
import net.minecraft.world.entity.ai.goal.RandomStrollGoal
import net.minecraft.world.entity.ai.goal.TemptGoal
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.pathfinder.PathType

class HorseshoeCrabEntity(entityType: EntityType<out HorseshoeCrabEntity>, world: Level) : Animal(entityType, world) {
    val idleAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout = 0

    init {
        this.setPathfindingMalus(PathType.WATER, 0.0f)
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder = createAnimalAttributes()
            .add(Attributes.MAX_HEALTH, 5.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ATTACK_DAMAGE, 1.0)
            .add(Attributes.ATTACK_KNOCKBACK, 0.0)

        fun canSpawn(
            type: EntityType<HorseshoeCrabEntity>,
            world: LevelAccessor,
            reason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource,
        ): Boolean {
            val seaLevel = world.seaLevel
            val shallowDepthMin = seaLevel - 10
            val shallowDepthMax = seaLevel + 4

            val blockBelow = world.getBlockState(pos.below())

            val shouldSpawn =
                pos.y in shallowDepthMin..shallowDepthMax
                    && blockBelow.isSolid
                    && world.canSeeSkyFromBelowWater(pos)

            if (shouldSpawn) {
                HorseshoeCrab.LOGGER.info("✓ Spawning horseshoe crab @ $pos)")
            } else {
                HorseshoeCrab.LOGGER.info(
                    "X Failed to spawn at $pos. {}, {}, {}",
                    pos.y in shallowDepthMin..shallowDepthMax,
                    blockBelow.isSolid,
                    world.canSeeSkyFromBelowWater(pos),
                )
            }

            return shouldSpawn
        }
    }

    override fun isPushedByFluid(): Boolean = false

    override fun canBeLeashed(): Boolean = true

    override fun isFood(stack: ItemStack): Boolean = stack.`is`(Items.KELP)

    override fun canBreatheUnderwater(): Boolean = true

    override fun checkSpawnObstruction(levelReader: LevelReader): Boolean = levelReader.isUnobstructed(this)

    override fun registerGoals() {
        this.goalSelector.addGoal(0, BreedGoal(this, 0.7))
        this.goalSelector.addGoal(1, TemptGoal(this, 0.7, Ingredient.of(Items.KELP), false))
        this.goalSelector.addGoal(2, FollowParentGoal(this, 1.05))
        this.goalSelector.addGoal(3, WanderInWaterGoal(this, 0.55))
        this.goalSelector.addGoal(4, WanderOnLandGoal(this, 0.55, 100))
    }

    private fun setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40
            this.idleAnimationState.start(this.tickCount)
        } else {
            this.idleAnimationTimeout--
        }
    }

    override fun getBreedOffspring(world: ServerLevel, entity: AgeableMob): AgeableMob? =
        ModEntities.HORSESHOE_CRAB.create(world, EntitySpawnReason.BREEDING)

    // Always restore air when in water
    override fun tick() {
        super.tick()

        if (this.level().isClientSide) {
            this.setupAnimationStates()
        }
    }

    private class WanderInWaterGoal(private val crab: HorseshoeCrabEntity, speed: Double) :
        MoveToBlockGoal(crab, if (crab.isBaby) 0.65 else speed, 24) {
        init {
            this.verticalSearchStart = -1
        }

        override fun canContinueToUse(): Boolean = !this.crab.isInWater &&
            this.tryTicks <= 1200 &&
            this.isValidTarget(this.crab.level(), this.blockPos)

        override fun canUse(): Boolean = if (!this.crab.isInWater) {
            super.canUse()
        } else {
            false
        }

        override fun shouldRecalculatePath(): Boolean = this.tryTicks % 160 == 0

        override fun isValidTarget(world: LevelReader, pos: BlockPos): Boolean = world.getBlockState(pos).`is`(Blocks.WATER)
    }

    private class WanderOnLandGoal(private val crab: HorseshoeCrabEntity, speed: Double, chance: Int) :
        RandomStrollGoal(crab, speed, chance) {
        override fun canUse(): Boolean = if (this.crab.isInWater) super.canUse() else false
    }
}
