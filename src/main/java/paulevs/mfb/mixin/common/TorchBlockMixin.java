package paulevs.mfb.mixin.common;

import net.minecraft.block.BaseBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import paulevs.mfb.block.MFBWallBlock;

@Mixin(TorchBlock.class)
public class TorchBlockMixin {
	@Redirect(method = "isFloorSupport", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/level/Level;getBlockId(III)I"
	))
	private int mfb_torchSupport(Level level, int x, int y, int z) {
		BaseBlock block = level.getBlockState(x, y, z).getBlock();
		if (block instanceof MFBWallBlock) return BaseBlock.FENCE.id;
		if (block instanceof FenceBlock) return BaseBlock.FENCE.id;
		return block.id;
	}
}
