package paulevs.mfb.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BaseBlock;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.BlockItem;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.AxisDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.mfb.block.MFBBlocks;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEHalfSlabBlock;

@Mixin(value = VBEHalfSlabBlock.class, remap = false)
public class VBEHalfSlabBlockMixin {
	@WrapOperation(method = "canUse", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/item/BlockItem;getBlock()Lnet/minecraft/block/BaseBlock;"
	))
	private BaseBlock mfb_getItemBLock(BlockItem item, Operation<BaseBlock> original) {
		BaseBlock block = item.getBlock();
		return block instanceof VBEHalfSlabBlock ? VBEHalfSlabBlock.class.cast(this) : block;
	}
	
	@ModifyExpressionValue(method = "canUse", at = @At(
		value = "INVOKE",
		target = "Lnet/modificationstation/stationapi/api/block/BlockState;isOf(Lnet/minecraft/block/BaseBlock;)Z"
	))
	private boolean mfb_canUse(boolean original, @Local BlockState state) {
		return state.getBlock() instanceof VBEHalfSlabBlock;
	}
	
	@WrapOperation(method = "canUse", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/block/BaseBlock;getDefaultState()Lnet/modificationstation/stationapi/api/block/BlockState;"
	))
	private BlockState mfb_getDefaultState(BaseBlock block, Operation<BlockState> original, @Local(ordinal = 0) BlockState state, @Local BlockItem item) {
		if (state.getBlock() == item.getBlock()) return original.call(block);
		return MFBBlocks.DOUBLE_SLAB.getDefaultState();
	}
	
	@SuppressWarnings("InvalidInjectorMethodSignature")
	@Inject(method = "canUse", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/level/Level;updateBlock(III)V",
		shift = Shift.BEFORE
	))
	private void mfb_updateBlockEntity(
		Level level, int x, int y, int z, PlayerBase player, CallbackInfoReturnable<Boolean> info,
		@Local(ordinal = 0) BlockState state, @Local BlockItem item
	) {
		if (level.getBlockEntity(x, y, z) instanceof DoubleSlabBlockEntity entity) {
			Direction dir = state.get(VBEBlockProperties.DIRECTION);
			if (dir.getDirection() == AxisDirection.NEGATIVE) {
				entity.bottomSlab = state;
				entity.topSlab = item.getBlock().getDefaultState().with(VBEBlockProperties.DIRECTION, dir.getOpposite());
			}
			else {
				entity.topSlab = state;
				entity.bottomSlab = item.getBlock().getDefaultState().with(VBEBlockProperties.DIRECTION, dir.getOpposite());
			}
		}
	}
}
