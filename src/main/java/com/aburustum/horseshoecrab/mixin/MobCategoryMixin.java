package com.aburustum.horseshoecrab.mixin;

import com.aburustum.horseshoecrab.CustomMobCategories;
import java.util.Arrays;
import net.minecraft.world.entity.MobCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobCategory.class)
public final class MobCategoryMixin {

    MobCategoryMixin(
        String enumname,
        int ordinal,
        String name,
        int spawnCap,
        boolean isFriendly,
        boolean isPersistent,
        int despawnDistance
    ) {
        throw new AssertionError();
    }

    @Shadow
    @Mutable
    @Final
    private static MobCategory[] $VALUES;

    @Unique
    private static MobCategory horseshoecrab$createCustomMobCategories(
        String enumname,
        int ordinal,
        CustomMobCategories mobCategory
    ) {
        return ((MobCategory) (Object) new MobCategoryMixin(
            mobCategory.name(),
            ordinal,
            mobCategory.gName,
            mobCategory.spawnCap,
            mobCategory.isFriendly,
            mobCategory.isPersistent,
            mobCategory.despawnDistance
        ));
    }

    @Inject(
        method = "<clinit>",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.PUTSTATIC,
            target = "Lnet/minecraft/world/entity/MobCategory;$VALUES:[Lnet/minecraft/world/entity/MobCategory;",
            shift = Shift.AFTER
        )
    )
    private static void injectEnum(CallbackInfo ci) {
        int vanillaSpawnGroupsLength = $VALUES.length;

        CustomMobCategories[] customMobCategories = CustomMobCategories.values();
        $VALUES = Arrays.copyOf($VALUES, vanillaSpawnGroupsLength + customMobCategories.length);

        for (int i = 0; i < customMobCategories.length; i++) {
            int pos = vanillaSpawnGroupsLength + i;
            CustomMobCategories c = customMobCategories[i];
            c.mobCategory = $VALUES[pos] = horseshoecrab$createCustomMobCategories(c.name(), pos, c);
        }
    }

}
