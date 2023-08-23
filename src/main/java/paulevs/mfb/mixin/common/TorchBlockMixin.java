package paulevs.mfb.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import paulevs.mfb.block.MFBWallBlock;

@Mixin(TorchBlock.class)
public class TorchBlockMixin {
	@WrapOperation(
		method = "isFloorSupport",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/level/Level;getBlockId(III)I")
	)
	private int mfb_torchSupport(Level level, int x, int y, int z, Operation<Integer> original) {
		BaseBlock block = level.getBlockState(x, y, z).getBlock();
		if (block instanceof MFBWallBlock) return BaseBlock.FENCE.id;
		if (block instanceof FenceBlock) return BaseBlock.FENCE.id;
		return original.call(level, x, y, z);
	}
}
