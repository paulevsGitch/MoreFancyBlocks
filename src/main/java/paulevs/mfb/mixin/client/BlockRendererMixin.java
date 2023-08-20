package paulevs.mfb.mixin.client;

import net.minecraft.block.BaseBlock;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.mfb.block.MFBWallBlock;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
	@Unique private final boolean[] mfb_canConnect = new boolean[4];
	
	@Shadow private BlockView blockView;
	
	@Shadow public abstract boolean renderFullCube(BaseBlock block, int x, int y, int z);
	
	@Inject(method = "renderFence", at = @At("HEAD"), cancellable = true)
	private void mfb_renderFence(BaseBlock block, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		if (!(block instanceof MFBWallBlock wallBlock)) return;
		if (!(blockView instanceof BlockStateView blockStateView)) return;
		
		byte count = 0;
		for (byte i = 0; i < 4; i++) {
			Direction dir = Direction.fromHorizontal(i);
			BlockState side = blockStateView.getBlockState(x + dir.getOffsetX(), y, z + dir.getOffsetZ());
			mfb_canConnect[i] = wallBlock.vbe_canConnect(side, dir);
			if (mfb_canConnect[i]) {
				float minX = 0.3125F;
				float minZ = 0.3125F;
				float maxX = 0.6875F;
				float maxZ = 0.6875F;
				float maxY = 0.8125F;
				
				if (dir.getOffsetX() < 0) { minX = 0; maxX = 0.5F; }
				if (dir.getOffsetX() > 0) { maxX = 1; minX = 0.5F; }
				if (dir.getOffsetZ() < 0) { minZ = 0; maxZ = 0.5F; }
				if (dir.getOffsetZ() > 0) { maxZ = 1; minZ = 0.5F; }
				
				wallBlock.setBoundingBox(minX, 0.0F, minZ, maxX, maxY, maxZ);
				renderFullCube(wallBlock, x, y, z);
				count++;
			}
		}
		
		if (count < 2 || count == 3 || (count == 2 && mfb_canConnect[0] != mfb_canConnect[2])) {
			wallBlock.setBoundingBox(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
			renderFullCube(wallBlock, x, y, z);
		}
		
		info.setReturnValue(true);
	}
}
