package carpetfixes.mixins.blockUpdates;

import carpetfixes.CFSettings;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

/**
 * When redstone dust gets redirected by having a block that redstone dust can point to next to it. It does not update
 * the original position that the redstone dust was looking at. This is known as redstone dust redirection, we fix this
 * by providing the valid updates.
 */

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlock_missingUpdateMixin extends Block {

    ThreadLocal<Boolean> needsUpdate = ThreadLocal.withInitial(() -> false);

    public RedstoneWireBlock_missingUpdateMixin(Settings settings) {
        super(settings);
    }


    @Inject(
            method = "prepare(Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldAccess;replaceWithStateForNeighborUpdate(" +
                            "Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;" +
                            "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;II)V"
            )
    )
    private void shouldUpdate(BlockState state, WorldAccess world, BlockPos pos,
                              int flags, int maxUpdateDepth, CallbackInfo ci) {
        needsUpdate.set(true);
    }


    @Inject(
            method = "prepare(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;" +
                    "Lnet/minecraft/util/math/BlockPos;II)V",
            at = @At("TAIL")
    )
    private void doUpdate(BlockState state, WorldAccess world, BlockPos pos, int flags, int d, CallbackInfo ci) {
        if (CFSettings.redstoneRedirectionMissingUpdateFix && needsUpdate.get()) {
            Set<BlockPos> set = Sets.newHashSet();
            set.add(pos);
            Direction[] var6 = Direction.values();
            for (Direction direction : var6) set.add(pos.offset(direction));
            for (BlockPos blockPos : set) ((World) world).updateNeighborsAlways(blockPos, this);
        }
        needsUpdate.set(false);
    }
}