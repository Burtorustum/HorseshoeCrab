package com.aburustum.horseshoecrab;

import net.minecraft.world.entity.MobCategory;

public enum CustomMobCategories {

    HORSESHOE_CRAB("crab", 10, true, false, 128);

    public MobCategory mobCategory;
    public final String gName;

    public final String name;
    public final int spawnCap;
    public final boolean isFriendly;
    public final boolean isPersistent;
    public final int despawnDistance;

    CustomMobCategories(
        final String name,
        final int spawnCap,
        final boolean isFriendly,
        final boolean isPersistent,
        final int despawnDistance
    ) {
        this.gName = HorseshoeCrab.MOD_ID + ":" + name;
        this.name = name;
        this.spawnCap = spawnCap;
        this.isFriendly = isFriendly;
        this.isPersistent = isPersistent;
        this.despawnDistance = despawnDistance;
    }
}
