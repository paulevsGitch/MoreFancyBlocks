package paulevs.mfb.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicBlockRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.BlockRenderManagerAccessor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.mfb.block.MFBDoubleSlabBlock;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;
import paulevs.vbe.render.BlockViewWrapper;

public class CustomBlockRenderer {
	private static final BlockViewWrapper VIEW = new BlockViewWrapper();
	private static final BlockRenderer RENDERER = new BlockRenderer(VIEW);
	private static boolean renderSlab = false;
	
	public static void mfb_renderDoubleSlab(BlockRenderManagerAccessor accessor, Block block, int x, int y, int z, ArsenicBlockRenderer renderer, CallbackInfoReturnable<Boolean> original, CallbackInfo info) {
		if (renderSlab) return;
		
		if (!(block instanceof MFBDoubleSlabBlock)) return;
		info.cancel();
		
		BlockView view = accessor.getBlockView();
		
		if (!(view.getBlockEntity(x, y, z) instanceof DoubleSlabBlockEntity entity)) {
			original.setReturnValue(false);
			return;
		}
		
		renderSlab = true;
		if (entity.bottomSlab != null) {
			VIEW.setData(view, entity.bottomSlab, x, y, z);
			original.setReturnValue(false);
			renderer.renderWorld(entity.bottomSlab.getBlock(), x, y, z, original);
			if (!original.getReturnValue()) {
				RENDERER.render(entity.bottomSlab.getBlock(), x, y, z);
			}
		}
		if (entity.topSlab != null) {
			VIEW.setData(view, entity.topSlab, x, y, z);
			original.setReturnValue(false);
			renderer.renderWorld(entity.topSlab.getBlock(), x, y, z, original);
			if (!original.getReturnValue()) {
				RENDERER.render(entity.topSlab.getBlock(), x, y, z);
			}
		}
		renderSlab = false;
		original.setReturnValue(true);
	}
	
	public static BlockView getView(BlockRenderManagerAccessor accessor, Operation<BlockView> original) {
		return renderSlab ? VIEW : original.call(accessor);
	}
}
