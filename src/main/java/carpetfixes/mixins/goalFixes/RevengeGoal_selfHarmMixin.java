package carpetfixes.mixins.goalFixes;

import carpetfixes.CFSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes entities with the revenge goal from being able to hurt themselves. Think of the kids Mojang!
 */

@Mixin(RevengeGoal.class)
public class RevengeGoal_selfHarmMixin {


    @Inject(
            method = "setMobEntityTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private void setMobEntityTarget(MobEntity mob, LivingEntity target, CallbackInfo ci) {
        if (CFSettings.selfHarmFix && mob == target) ci.cancel();
    }
}
