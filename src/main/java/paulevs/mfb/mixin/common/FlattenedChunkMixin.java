package paulevs.mfb.mixin.common;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.util.maths.BlockPos;
import net.modificationstation.stationapi.impl.world.chunk.FlattenedChunk;
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
	
	@SuppressWarnings("unchecked")
	@Inject(method = "setBlockEntity", at = @At("HEAD"), cancellable = true)
	private void mfb_skipTileEntityCheck(int dx, int dy, int dz, BlockEntity entity, CallbackInfo info) {
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
