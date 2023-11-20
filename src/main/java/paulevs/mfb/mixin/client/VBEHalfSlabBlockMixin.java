package paulevs.mfb.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import paulevs.vbe.block.VBEHalfSlabBlock;

@Mixin(VBEHalfSlabBlock.class)
public abstract class VBEHalfSlabBlockMixin {
	@ModifyReturnValue(
		method = "isSideRendered(Lnet/minecraft/level/BlockView;IIII)Z",
		at = @At("RETURN")
	)
	private boolean mfb_isSideRendered(boolean render, @Local BlockView view, @Local(index = 2) int x, @Local(index = 3) int y, @Local(index = 4) int z) {
		if (!render && view instanceof BlockStateView blockStateView) {
			BlockState state = blockStateView.getBlockState(x, y, z);
			render = !state.isOf(Block.class.cast(this));
		}
		return render;
	}
}
