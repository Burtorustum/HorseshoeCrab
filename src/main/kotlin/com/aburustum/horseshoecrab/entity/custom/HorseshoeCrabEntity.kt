package com.aburustum.horseshoecrab.entity.custom

import com.aburustum.horseshoecrab.entity.ModEntities
import net.minecraft.block.Blocks
import net.minecraft.entity.AnimationState
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.AnimalMateGoal
import net.minecraft.entity.ai.goal.FollowParentGoal
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal
import net.minecraft.entity.ai.goal.TemptGoal
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.passive.TurtleEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class HorseshoeCrabEntity(
    entityType: EntityType<out AnimalEntity>,
    world: World,
) : AnimalEntity(entityType, world) {
    val idleAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout = 0
    private var travelPos: BlockPos? = null

    init {
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f)
        this.setPathfindingPenalty(PathNodeType.DOOR_IRON_CLOSED, -1.0f)
        this.setPathfindingPenalty(PathNodeType.DOOR_WOOD_CLOSED, -1.0f)
        this.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1.0f)
    }

    companion object {
        fun createAttributes(): DefaultAttributeContainer.Builder =
            createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.05)
                .add(EntityAttributes.STEP_HEIGHT, 0.0)

        fun canSpawn(
            type: EntityType<HorseshoeCrabEntity>,
            world: WorldAccess,
            spawnReason: SpawnReason,
            pos: BlockPos,
            random: Random,
        ): Boolean = pos.y < world.seaLevel + 2 && isLightLevelValidForNaturalSpawn(world, pos)
    }

    override fun initGoals() {
        this.goalSelector.add(0, WanderInWaterGoal(this, 0.55))

        this.goalSelector.add(1, AnimalMateGoal(this, 0.7))
        this.goalSelector.add(2, TemptGoal(this, 0.7, Ingredient.ofItem(Items.KELP), false))

        this.goalSelector.add(3, FollowParentGoal(this, 1.05)) // TODO: create a "follow in a line" goal
        this.goalSelector.add(5, WanderOnLandGoal(this, 0.55, 100))
    }

    private class WanderInWaterGoal(
        private val crab: HorseshoeCrabEntity,
        speed: Double,
    ) : MoveToTargetPosGoal(crab, if (crab.isBaby) 0.65 else speed, 24) {
        init {
            this.lowestY = -1
        }

        override fun shouldContinue(): Boolean =
            !this.crab.isTouchingWater &&
                this.tryingTime <= 1200 &&
                this.isTargetPos(this.crab.entityWorld, this.targetPos)

        override fun canStart(): Boolean =
            if (this.crab.isBaby && !this.crab.isTouchingWater) {
                super.canStart()
            } else if (!this.crab.isTouchingWater) {
                super.canStart()
            } else {
                false
            }

        override fun shouldResetPath(): Boolean = this.tryingTime % 160 == 0

        override fun isTargetPos(
            world: WorldView,
            pos: BlockPos?,
        ): Boolean = world.getBlockState(pos).isOf(Blocks.WATER)
    }

    private class WanderOnLandGoal(
        private val crab: HorseshoeCrabEntity,
        speed: Double,
        chance: Int,
    ) : WanderAroundGoal(crab, speed, chance) {
        override fun canStart(): Boolean = if (this.crab.isTouchingWater) super.canStart() else false
    }

    private fun setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40
            this.idleAnimationState.start(this.age)
        } else {
            this.idleAnimationTimeout--
        }
    }

    override fun tick() {
        super.tick()

        if (this.entityWorld.isClient) {
            this.setupAnimationStates()
        }
    }

    override fun isBreedingItem(stack: ItemStack): Boolean = stack.isOf(Items.KELP)

    override fun createChild(
        world: ServerWorld,
        entity: PassiveEntity,
    ): PassiveEntity? = ModEntities.HORSESHOE_CRAB.create(world, SpawnReason.BREEDING)

    // TODO: create navigations for following in line, getting beached
    private class HorseshoeCrabSwimNavigation(
        owner: TurtleEntity,
        world: World?,
    ) : AmphibiousSwimNavigation(owner, world) {
        override fun isValidPosition(pos: BlockPos): Boolean {
            val e = this.entity
            if (e is HorseshoeCrabEntity) {
                if (e.travelPos != null) {
                    return this.world.getBlockState(pos).isOf(Blocks.WATER) &&
                        this.world.getBlockState(pos.down()).isOpaqueFullCube
                }
            }

            return !this.world.getBlockState(pos.down()).isAir
        }
    }
}
