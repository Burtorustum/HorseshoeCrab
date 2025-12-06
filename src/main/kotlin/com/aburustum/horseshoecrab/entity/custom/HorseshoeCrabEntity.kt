package com.aburustum.horseshoecrab.entity.custom

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
import net.minecraft.world.entity.ai.goal.FollowParentGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal
import net.minecraft.world.entity.ai.goal.RandomStrollGoal
import net.minecraft.world.entity.ai.goal.TemptGoal
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.pathfinder.PathType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HorseshoeCrabEntity(entityType: EntityType<out HorseshoeCrabEntity>, world: Level) : Animal(entityType, world) {
    val idleAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout = 0

    var isUpsideDown: Boolean = false
    var orientationTransition: Float = 0.0f

    var congoLinePartner: HorseshoeCrabEntity? = null
    var isCongoLineLeader: Boolean = false

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
            val shallowDepthMin = seaLevel - 8
            val shallowDepthMax = seaLevel + 4

            return pos.y in shallowDepthMin..shallowDepthMax
                // && world.isWater(pos)
                && world.getBlockState(pos.below()).isSolid
                && world.canSeeSkyFromBelowWater(pos)
        }
    }

    override fun isPushedByFluid(): Boolean = false

    override fun canBeLeashed(): Boolean = true

    override fun isFood(stack: ItemStack): Boolean = stack.`is`(Items.KELP)

    override fun canBreatheUnderwater(): Boolean = true

    override fun registerGoals() {
        this.goalSelector.addGoal(0, CongoLineBreedingGoal(this, 0.7))
        this.goalSelector.addGoal(1, CrawlOnBottomGoal(this))

        this.goalSelector.addGoal(2, TemptGoal(this, 0.7, Ingredient.of(Items.KELP), false))

        this.goalSelector.addGoal(3, FollowParentGoal(this, 1.05))
        this.goalSelector.addGoal(4, WanderInWaterGoal(this, 0.55))
        this.goalSelector.addGoal(5, WanderOnLandGoal(this, 0.55, 100))
    }

    private fun setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40
            this.idleAnimationState.start(this.tickCount)
        } else {
            this.idleAnimationTimeout--
        }
    }

    /**
     * Updates the orientation state based on whether the entity is swimming
     * (in water without ground below)
     */
    private fun updateOrientation() {
        // Check if entity is swimming: in water and no solid ground within 1 block below
        val isSwimming =
            if (this.isInWater) {
                val posBelow = this.blockPosition().below()
                val blockBelow = this.level().getBlockState(posBelow)
                !blockBelow.isRedstoneConductor(this.level(), posBelow)
            } else {
                false
            }

        // Update target orientation state
        val targetUpsideDown = isSwimming

        // Smooth interpolation for orientation transition
        val transitionSpeed = 0.1f
        if (targetUpsideDown != isUpsideDown) {
            // Transitioning between states
            if (targetUpsideDown) {
                // Transitioning to upside-down
                orientationTransition += transitionSpeed
                if (orientationTransition >= 1.0f) {
                    orientationTransition = 1.0f
                    isUpsideDown = true
                }
            } else {
                // Transitioning to upright
                orientationTransition -= transitionSpeed
                if (orientationTransition <= 0.0f) {
                    orientationTransition = 0.0f
                    isUpsideDown = false
                }
            }
        } else {
            // Already in target state, ensure transition value matches
            orientationTransition = if (isUpsideDown) 1.0f else 0.0f
        }

        // Clamp to valid range [0.0, 1.0]
        orientationTransition = orientationTransition.coerceIn(0.0f, 1.0f)
    }

    override fun getBreedOffspring(world: ServerLevel, entity: AgeableMob): AgeableMob? =
        ModEntities.HORSESHOE_CRAB.create(world, EntitySpawnReason.BREEDING)

    override fun createNavigation(world: Level): PathNavigation = BottomCrawlingNavigation(this, world)

    // Always restore air when in water
    override fun tick() {
        super.tick()

        if (this.level().isClientSide) {
            this.setupAnimationStates()
        }

        // Update orientation state every tick
        this.updateOrientation()
    }

    /**
     * Custom navigation for horseshoe crabs that prefers bottom surfaces
     */
    private class BottomCrawlingNavigation(entity: HorseshoeCrabEntity, world: Level) : AmphibiousPathNavigation(entity, world) {
        override fun isStableDestination(pos: BlockPos): Boolean {
            val blockState = this.level.getBlockState(pos)
            val blockBelow = this.level.getBlockState(pos.below())

            // If in water, require solid block beneath
            if (blockState.`is`(Blocks.WATER)) {
                return blockBelow.isRedstoneConductor(this.level, pos.below())
            }

            // On land, just ensure we're not in air
            return !blockBelow.isAir
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

        override fun canUse(): Boolean = if (this.crab.isBaby && !this.crab.isInWater) {
            super.canUse()
        } else if (!this.crab.isInWater) {
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

    /**
     * AI goal that makes horseshoe crabs sink to the bottom when in water
     */
    private class CrawlOnBottomGoal(private val crab: HorseshoeCrabEntity) : Goal() {
        override fun canUse(): Boolean {
            // Start if in water and not touching ground
            return crab.isInWater && !crab.onGround()
        }

        override fun canContinueToUse(): Boolean {
            // Continue while in water and not on ground
            return crab.isInWater && !crab.onGround()
        }

        override fun tick() {
            // Apply downward velocity to sink to bottom
            val velocity = crab.deltaMovement
            crab.setDeltaMovement(velocity.x, velocity.y - 0.05, velocity.z)
        }
    }

    /**
     * Congo line breeding goal that implements formation-based breeding behavior
     */
    private class CongoLineBreedingGoal(private val crab: HorseshoeCrabEntity, private val speed: Double) : Goal() {
        private var partner: HorseshoeCrabEntity? = null
        private var formationTimer: Int = 0
        private val formationDuration: Int = 60 // ticks (3 seconds)
        private val alignmentThreshold: Double = 1.5 // blocks
        private val detectionRange: Double = 8.0 // blocks

        override fun canUse(): Boolean {
            // Only start if this crab is in love mode (fed kelp)
            if (!crab.isInLove) {
                return false
            }

            // Find a partner within range that is also in love mode
            val potentialPartner = findPartner()
            if (potentialPartner != null) {
                partner = potentialPartner
                determineRoles(potentialPartner)
                return true
            }

            return false
        }

        override fun canContinueToUse(): Boolean {
            val currentPartner = partner

            // Stop if partner is gone or no longer in love mode
            if (currentPartner == null || !currentPartner.isAlive || !currentPartner.isInLove || !crab.isInLove) {
                return false
            }

            // Stop if partner is too far away
            if (crab.distanceToSqr(currentPartner) > detectionRange * detectionRange) {
                return false
            }

            return true
        }

        override fun start() {
            formationTimer = 0
        }

        override fun stop() {
            partner = null
            crab.congoLinePartner = null
            crab.isCongoLineLeader = false
            formationTimer = 0
        }

        override fun tick() {
            val currentPartner = partner ?: return

            if (crab.isCongoLineLeader) {
                // Leader just needs to stay relatively still or move slowly
                // The follower will position itself behind the leader
                crab.lookAt(currentPartner, 30.0f, 30.0f)
            } else {
                // Follower moves to position behind leader
                moveToFormation(currentPartner)
            }

            // Check if formation is complete
            if (isInFormation(currentPartner)) {
                formationTimer++

                // If formation maintained for required duration, breed
                if (formationTimer >= formationDuration) {
                    breed(currentPartner)
                }
            } else {
                // Reset timer if formation breaks
                formationTimer = 0
            }
        }

        /**
         * Find a suitable partner for congo line breeding
         */
        private fun findPartner(): HorseshoeCrabEntity? {
            val nearbyEntities =
                crab.level().getEntitiesOfClass(
                    HorseshoeCrabEntity::class.java,
                    crab.boundingBox.inflate(detectionRange),
                    { entity ->
                        entity != crab &&
                            entity.isAlive &&
                            entity.isInLove &&
                            entity.congoLinePartner == null
                    },
                )

            return nearbyEntities.minByOrNull { crab.distanceToSqr(it) }
        }

        /**
         * Determine which crab is leader and which is follower
         * Uses UUID comparison for deterministic role assignment
         */
        private fun determineRoles(partner: HorseshoeCrabEntity) {
            val isLeader = crab.uuid < partner.uuid

            crab.isCongoLineLeader = isLeader
            crab.congoLinePartner = partner

            partner.isCongoLineLeader = !isLeader
            partner.congoLinePartner = crab
        }

        /**
         * Move follower to position behind leader
         */
        private fun moveToFormation(leader: HorseshoeCrabEntity) {
            // Calculate position behind leader
            val leaderYaw = Math.toRadians(leader.yRot.toDouble())
            val offsetX = -sin(leaderYaw) * 1.5 // 1.5 blocks behind
            val offsetZ = cos(leaderYaw) * 1.5

            val targetX = leader.x + offsetX
            val targetZ = leader.z + offsetZ
            val targetY = leader.y

            // Navigate to target position
            val targetPos = BlockPos(targetX.toInt(), targetY.toInt(), targetZ.toInt())
            crab.navigation.moveTo(targetX, targetY, targetZ, speed)

            // Look at leader
            crab.lookAt(leader, 30.0f, 30.0f)
        }

        /**
         * Check if follower is properly aligned behind leader
         */
        private fun isInFormation(partner: HorseshoeCrabEntity): Boolean {
            val leader = if (crab.isCongoLineLeader) crab else partner
            val follower = if (crab.isCongoLineLeader) partner else crab

            // Calculate expected position behind leader
            val leaderYaw = Math.toRadians(leader.yRot.toDouble())
            val expectedOffsetX = -sin(leaderYaw) * 1.5
            val expectedOffsetZ = cos(leaderYaw) * 1.5

            val expectedX = leader.x + expectedOffsetX
            val expectedZ = leader.z + expectedOffsetZ

            // Calculate actual distance from expected position
            val dx = follower.x - expectedX
            val dz = follower.z - expectedZ
            val distance = sqrt(dx * dx + dz * dz)

            return distance <= alignmentThreshold
        }

        /**
         * Trigger breeding when formation is complete
         */
        private fun breed(partner: HorseshoeCrabEntity) {
            // Use Minecraft's built-in breeding logic
            crab.spawnChildFromBreeding(crab.level() as ServerLevel, partner)

            // Reset love mode
            crab.resetLove()
            partner.resetLove()

            // Clear congo line state
            stop()

            // If partner has this goal, stop it too
            partner.congoLinePartner = null
            partner.isCongoLineLeader = false
        }
    }
}
