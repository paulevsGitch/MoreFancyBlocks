package paulevs.mfb.mixin.common;

import net.minecraft.block.entity.BaseBlockEntity;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.util.maths.BlockPos;
import net.modificationstation.stationapi.impl.level.chunk.FlattenedChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;

@Mixin(value = FlattenedChunk.class, remap = false)
public abstract class FlattenedChunkMixin extends Chunk {
	public FlattenedChunkMixin(Level arg, int i, int j) {
		super(arg, i, j);
	}
	/*@WrapOperation(
		method = "placeTileEntity",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/level/Level;getBlockId(III)I")
	)
	private int mfb_skipTileEntityCheck(Level level, int x, int y, int z, Operation<Integer> original) {
		BaseBlock block = level.getBlockState(x, y, z).getBlock();
		if (block instanceof MFBDoubleSlabBlock) return BaseBlock.FURNACE.id;
		return original.call(level, x, y, z);
	}*/
	
	@SuppressWarnings("unchecked")
	@Inject(method = "setBlockEntity", at = @At("HEAD"), cancellable = true)
	private void mfb_skipTileEntityCheck(int dx, int dy, int dz, BaseBlockEntity entity, CallbackInfo info) {
		if (entity instanceof DoubleSlabBlockEntity) {
			BlockPos pos = new BlockPos(dx, dy, dz);
			entity.level = this.level;
			entity.x = this.x << 4 | dx;
			entity.y = dy;
			entity.z = this.z << 4 | dz;
			blockEntities.put(pos, entity);
			info.cancel();
		}
	}
}
