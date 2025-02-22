package carpetfixes.mixins.entityFixes.growUpCollisionFix;

import carpetfixes.CFSettings;
import carpetfixes.patches.ExtendedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes zombies doing the wrong collision calculation when growing up
 */

@Mixin(ZombieEntity.class)
public abstract class ZombieEntity_dimensionsMixin extends Entity {

    public ZombieEntity_dimensionsMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @Inject(
            method = "onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/ZombieEntity;calculateDimensions()V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void calculateCustomDimensions(TrackedData<?> data, CallbackInfo ci) {
        if (CFSettings.entityGrowingUpCollisionClippingFix) {
            ((ExtendedEntity)this).calculateDimensionsWithoutHeight();
            super.onTrackedDataSet(data);
            ci.cancel();
        }
    }
}
