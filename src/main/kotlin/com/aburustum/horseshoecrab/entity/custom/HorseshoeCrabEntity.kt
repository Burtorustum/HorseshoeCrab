package com.aburustum.horseshoecrab.entity.custom

import com.aburustum.horseshoecrab.entity.ModEntities
import net.minecraft.block.Blocks
import net.minecraft.entity.AnimationState
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.ai.goal.FollowParentGoal
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal
import net.minecraft.entity.ai.goal.TemptGoal
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation
import net.minecraft.entity.ai.pathing.EntityNavigation
import net.minecraft.entity.ai.pathing.PathNodeType
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HorseshoeCrabEntity(entityType: EntityType<out HorseshoeCrabEntity>, world: World) : AnimalEntity(entityType, world) {
    val idleAnimationState: AnimationState = AnimationState()
    private var idleAnimationTimeout = 0

    var isUpsideDown: Boolean = false
    var orientationTransition: Float = 0.0f

    var congoLinePartner: HorseshoeCrabEntity? = null
    var isCongoLineLeader: Boolean = false

    init {
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0f)
    }

    companion object {
        fun createAttributes(): DefaultAttributeContainer.Builder = createAnimalAttributes()
            .add(EntityAttributes.MAX_HEALTH, 5.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
            .add(EntityAttributes.ATTACK_DAMAGE, 1.0)
            .add(EntityAttributes.ATTACK_KNOCKBACK, 0.0)

        fun canSpawn(
            type: EntityType<HorseshoeCrabEntity>,
            world: WorldAccess,
            reason: SpawnReason,
            pos: BlockPos,
            random: Random,
        ): Boolean {
            val seaLevel = world.seaLevel
            val shallowDepthMin = seaLevel - 8
            val shallowDepthMax = seaLevel + 4

            return pos.y in shallowDepthMin..shallowDepthMax
                // && world.isWater(pos)
                && world.getBlockState(pos.down()).isSolid
                && world.isSkyVisibleAllowingSea(pos)
        }
    }

    override fun isPushedByFluids(): Boolean = false

    override fun canBeLeashed(): Boolean = true

    override fun isBreedingItem(stack: ItemStack): Boolean = stack.isOf(Items.KELP)

    override fun canBreatheInWater(): Boolean = true

    override fun getMaxAir(): Int = 6000 // 5 minutes of air (20 ticks per second * 60 * 5)

    override fun canSpawn(world: WorldAccess?, spawnReason: SpawnReason?): Boolean = super.canSpawn(world, spawnReason)

    override fun initGoals() {
        this.goalSelector.add(0, CongoLineBreedingGoal(this, 0.7))
        this.goalSelector.add(1, CrawlOnBottomGoal(this))

        this.goalSelector.add(2, TemptGoal(this, 0.7, Ingredient.ofItem(Items.KELP), false))

        this.goalSelector.add(3, FollowParentGoal(this, 1.05))
        this.goalSelector.add(4, WanderInWaterGoal(this, 0.55))
        this.goalSelector.add(5, WanderOnLandGoal(this, 0.55, 100))
    }

    private fun setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40
            this.idleAnimationState.start(this.age)
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
            if (this.isTouchingWater) {
                val posBelow = this.blockPos.down()
                val blockBelow = this.entityWorld.getBlockState(posBelow)
                !blockBelow.isSolidBlock(this.entityWorld, posBelow)
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

    override fun createChild(world: ServerWorld, entity: PassiveEntity): PassiveEntity? =
        ModEntities.HORSESHOE_CRAB.create(world, SpawnReason.BREEDING)

    override fun createNavigation(world: World): EntityNavigation = BottomCrawlingNavigation(this, world)

    // Always restore air when in water
    override fun tick() {
        super.tick()

        if (this.entityWorld.isClient) {
            this.setupAnimationStates()
        }

        // Restore air when in water (prevents drowning)
        if (this.isTouchingWater) {
            this.air = this.maxAir
        }

        // Update orientation state every tick
        this.updateOrientation()
    }

    /**
     * Custom navigation for horseshoe crabs that prefers bottom surfaces
     */
    private class BottomCrawlingNavigation(entity: HorseshoeCrabEntity, world: World) : AmphibiousSwimNavigation(entity, world) {
        override fun isValidPosition(pos: BlockPos): Boolean {
            val blockState = this.world.getBlockState(pos)
            val blockBelow = this.world.getBlockState(pos.down())

            // If in water, require solid block beneath
            if (blockState.isOf(Blocks.WATER)) {
                return blockBelow.isSolidBlock(this.world, pos.down())
            }

            // On land, just ensure we're not in air
            return !blockBelow.isAir
        }
    }

    private class WanderInWaterGoal(private val crab: HorseshoeCrabEntity, speed: Double) :
        MoveToTargetPosGoal(crab, if (crab.isBaby) 0.65 else speed, 24) {
        init {
            this.lowestY = -1
        }

        override fun shouldContinue(): Boolean = !this.crab.isTouchingWater &&
            this.tryingTime <= 1200 &&
            this.isTargetPos(this.crab.entityWorld, this.targetPos)

        override fun canStart(): Boolean = if (this.crab.isBaby && !this.crab.isTouchingWater) {
            super.canStart()
        } else if (!this.crab.isTouchingWater) {
            super.canStart()
        } else {
            false
        }

        override fun shouldResetPath(): Boolean = this.tryingTime % 160 == 0

        override fun isTargetPos(world: WorldView, pos: BlockPos?): Boolean = world.getBlockState(pos).isOf(Blocks.WATER)
    }

    private class WanderOnLandGoal(private val crab: HorseshoeCrabEntity, speed: Double, chance: Int) :
        WanderAroundGoal(crab, speed, chance) {
        override fun canStart(): Boolean = if (this.crab.isTouchingWater) super.canStart() else false
    }

    /**
     * AI goal that makes horseshoe crabs sink to the bottom when in water
     */
    private class CrawlOnBottomGoal(private val crab: HorseshoeCrabEntity) : Goal() {
        override fun canStart(): Boolean {
            // Start if in water and not touching ground
            return crab.isTouchingWater && !crab.isOnGround
        }

        override fun shouldContinue(): Boolean {
            // Continue while in water and not on ground
            return crab.isTouchingWater && !crab.isOnGround
        }

        override fun tick() {
            // Apply downward velocity to sink to bottom
            val velocity = crab.velocity
            crab.setVelocity(velocity.x, velocity.y - 0.05, velocity.z)
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

        override fun canStart(): Boolean {
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

        override fun shouldContinue(): Boolean {
            val currentPartner = partner

            // Stop if partner is gone or no longer in love mode
            if (currentPartner == null || !currentPartner.isAlive || !currentPartner.isInLove || !crab.isInLove) {
                return false
            }

            // Stop if partner is too far away
            if (crab.squaredDistanceTo(currentPartner) > detectionRange * detectionRange) {
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
                crab.lookAtEntity(currentPartner, 30.0f, 30.0f)
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
                crab.entityWorld.getEntitiesByClass(
                    HorseshoeCrabEntity::class.java,
                    crab.boundingBox.expand(detectionRange),
                    { entity ->
                        entity != crab &&
                            entity.isAlive &&
                            entity.isInLove &&
                            entity.congoLinePartner == null
                    },
                )

            return nearbyEntities.minByOrNull { crab.squaredDistanceTo(it) }
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
            val leaderYaw = Math.toRadians(leader.yaw.toDouble())
            val offsetX = -sin(leaderYaw) * 1.5 // 1.5 blocks behind
            val offsetZ = cos(leaderYaw) * 1.5

            val targetX = leader.x + offsetX
            val targetZ = leader.z + offsetZ
            val targetY = leader.y

            // Navigate to target position
            val targetPos = BlockPos(targetX.toInt(), targetY.toInt(), targetZ.toInt())
            crab.navigation.startMovingTo(targetX, targetY, targetZ, speed)

            // Look at leader
            crab.lookAtEntity(leader, 30.0f, 30.0f)
        }

        /**
         * Check if follower is properly aligned behind leader
         */
        private fun isInFormation(partner: HorseshoeCrabEntity): Boolean {
            val leader = if (crab.isCongoLineLeader) crab else partner
            val follower = if (crab.isCongoLineLeader) partner else crab

            // Calculate expected position behind leader
            val leaderYaw = Math.toRadians(leader.yaw.toDouble())
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
            crab.breed(crab.entityWorld as ServerWorld, partner)

            // Reset love mode
            crab.resetLoveTicks()
            partner.resetLoveTicks()

            // Clear congo line state
            stop()

            // If partner has this goal, stop it too
            partner.congoLinePartner = null
            partner.isCongoLineLeader = false
        }
    }
}
