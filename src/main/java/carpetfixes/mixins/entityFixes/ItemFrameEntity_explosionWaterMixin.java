package carpetfixes.mixins.entityFixes;

import carpetfixes.CFSettings;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Item frames usually break when near an explosion. Although apparently if they are in water they shouldn't break.
 * So we modify the item frames damage check so that if it takes explosion damage, it first checks if its in water
 * and if so, negates the damage.
 * TODO: That is not at all what this bug report should be fixing xD
 */

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntity_explosionWaterMixin {

    ItemFrameEntity self = (ItemFrameEntity)(Object)this;


    @Inject(
            method = "damage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isInvulnerableOrWater(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (CFSettings.explosionBreaksItemFrameInWaterFix && source.isIn(DamageTypeTags.IS_EXPLOSION) &&
                self.getWorld().getFluidState(self.getBlockPos()).getFluid().matchesType(Fluids.WATER))
            cir.setReturnValue(true);
    }
}
