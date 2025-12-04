package com.aburustum.horseshoecrab.entity.custom

import com.aburustum.horseshoecrab.entity.ModEntities
import net.minecraft.entity.AnimationState
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

class HorseshoeCrabEntity(
    entityType: EntityType<out AnimalEntity>,
    world: World,
) : AnimalEntity(entityType, world) {
    val idleAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout = 0

    companion object {
        fun createAttributes(): DefaultAttributeContainer.Builder =
            createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.STEP_HEIGHT, 0.5)
    }

    override fun initGoals() {
        this.goalSelector.add(0, MoveIntoWaterGoal(this))

        this.goalSelector.add(1, AnimalMateGoal(this, 1.0))
        this.goalSelector.add(2, TemptGoal(this, 1.15, Ingredient.ofItem(Items.KELP), false))

        this.goalSelector.add(3, FollowParentGoal(this, 1.05)) // TODO: create a "follow in a line" goal
        this.goalSelector.add(5, WanderAroundGoal(this, 1.0))
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
}
