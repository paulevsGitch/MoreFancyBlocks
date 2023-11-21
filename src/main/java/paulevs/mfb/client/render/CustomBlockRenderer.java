package paulevs.mfb.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicBlockRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.BlockRenderManagerAccessor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.mfb.block.MFBDoubleSlabBlock;
import paulevs.mfb.block.MFBFullOctablock;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;
import paulevs.mfb.block.blockentity.FullOctaBlockEntity;
import paulevs.vbe.render.BlockViewWrapper;

public class CustomBlockRenderer {
	private static final BlockViewWrapper VIEW = new BlockViewWrapper();
	private static final BlockRenderer RENDERER = new BlockRenderer(VIEW);
	private static boolean isRendering = false;
	
	public static void render(BlockRenderManagerAccessor accessor, Block block, int x, int y, int z, ArsenicBlockRenderer renderer, CallbackInfoReturnable<Boolean> original, CallbackInfo info) {
		if (isRendering) return;
		
		BlockView view = accessor.getBlockView();
		
		if (block instanceof MFBDoubleSlabBlock) {
			renderDoubleSlab(view, x, y, z, renderer, original);
			info.cancel();
		}
		else if (block instanceof MFBFullOctablock) {
			renderFullOctablock(view, x, y, z, renderer, original);
			info.cancel();
		}
	}
	
	public static BlockView getView(BlockRenderManagerAccessor accessor, Operation<BlockView> original) {
		return isRendering ? VIEW : original.call(accessor);
	}
	
	private static void renderDoubleSlab(BlockView view, int x, int y, int z, ArsenicBlockRenderer renderer, CallbackInfoReturnable<Boolean> original) {
		if (!(view.getBlockEntity(x, y, z) instanceof DoubleSlabBlockEntity entity)) {
			original.setReturnValue(false);
			return;
		}
		
		isRendering = true;
		
		if (entity.bottomSlab != null) {
			renderBlockState(view, x, y, z, entity.bottomSlab, renderer, original);
		}
		if (entity.topSlab != null) {
			renderBlockState(view, x, y, z, entity.topSlab, renderer, original);
		}
		
		isRendering = false;
		
		original.setReturnValue(true);
	}
	
	private static void renderFullOctablock(BlockView view, int x, int y, int z, ArsenicBlockRenderer renderer, CallbackInfoReturnable<Boolean> original) {
		if (!(view.getBlockEntity(x, y, z) instanceof FullOctaBlockEntity entity)) {
			original.setReturnValue(false);
			return;
		}
		
		isRendering = true;
		
		for (byte i = 0; i < entity.states.length; i++) {
			BlockState state = entity.states[i];
			if (state == null) continue;
			renderBlockState(view, x, y, z, state, renderer, original);
		}
		
		isRendering = false;
		
		original.setReturnValue(true);
	}
	
	private static void renderBlockState(BlockView view, int x, int y, int z, BlockState state, ArsenicBlockRenderer renderer, CallbackInfoReturnable<Boolean> original) {
		VIEW.setData(view, state, x, y, z);
		original.setReturnValue(false);
		renderer.renderWorld(state.getBlock(), x, y, z, original);
		if (!original.getReturnValue()) {
			RENDERER.render(state.getBlock(), x, y, z);
		}
	}
}
