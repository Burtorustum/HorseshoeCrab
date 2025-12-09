package com.aburustum.horseshoecrab.entity.custom

import com.aburustum.horseshoecrab.entity.ModEntities
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.SpawnGroupData
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
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

// TODO Ideas:
//  - male/female variants determine partners when mating season
//  - disable player breeding, change spawn rules when full or new moon to be on beach and in mating state
//  - make Bucketable
//  - use nautilus as food when available
//  - Make swim upside down
//  - goal to prefer walking along bottom of water over swimming
//  - goal to return to water from land (except when full/new moon and still mating)
class HorseshoeCrabEntity(entityType: EntityType<out HorseshoeCrabEntity>, world: Level) : Animal(entityType, world) {
    val idleAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout = 0

    init {
        this.setPathfindingMalus(PathType.WATER, 0.0f)
    }

    companion object {
        val DATA_ID_TYPE_VARIANT: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(HorseshoeCrabEntity::class.java, EntityDataSerializers.INT)

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

            return pos.y in shallowDepthMin..shallowDepthMax
                && blockBelow.isSolid
                && world.canSeeSkyFromBelowWater(pos)
        }
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_ID_TYPE_VARIANT, 0)
    }

    fun getVariant(): HorseshoeCrabVariant = HorseshoeCrabVariant.byId(this.getTypeVariant() and 255)

    private fun getTypeVariant(): Int = this.entityData.get(DATA_ID_TYPE_VARIANT)

    private fun setVariant(variant: HorseshoeCrabVariant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant.id and 255)
    }

    override fun saveWithoutId(valueOutput: ValueOutput) {
        super.saveWithoutId(valueOutput)
        valueOutput.putInt("Variant", this.getTypeVariant())
    }

    override fun load(valueInput: ValueInput) {
        super.load(valueInput)
        this.entityData.set(DATA_ID_TYPE_VARIANT, valueInput.getIntOr("Variant", 0))
    }

    override fun finalizeSpawn(
        serverLevelAccessor: ServerLevelAccessor,
        difficultyInstance: DifficultyInstance,
        entitySpawnReason: EntitySpawnReason,
        spawnGroupData: SpawnGroupData?,
    ): SpawnGroupData {
        val variant: HorseshoeCrabVariant = Util.getRandom(HorseshoeCrabVariant.entries.toTypedArray(), this.random)
        setVariant(variant)
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData)!!
    }

    override fun isPushedByFluid(): Boolean = false

    override fun canBeLeashed(): Boolean = true

    override fun isFood(stack: ItemStack): Boolean = stack.`is`(Items.KELP) // TODO: use nautilus as food in 1.21.11 or add actual food

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

    override fun getBreedOffspring(world: ServerLevel, entity: AgeableMob): AgeableMob? {
        val offspring = ModEntities.HORSESHOE_CRAB.create(world, EntitySpawnReason.BREEDING)
        val variant: HorseshoeCrabVariant = Util.getRandom(HorseshoeCrabVariant.entries.toTypedArray(), this.random)
        offspring?.setVariant(variant)
        return offspring
    }

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
