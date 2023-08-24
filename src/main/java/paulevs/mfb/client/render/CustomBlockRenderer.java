package paulevs.mfb.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BaseBlock;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicBlockRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.BlockRendererAccessor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.mfb.block.MFBDoubleSlabBlock;
import paulevs.mfb.block.WrappedBlockView;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;
import paulevs.vbe.block.VBEBlockProperties;

public class CustomBlockRenderer {
	private static final WrappedBlockView VIEW = new WrappedBlockView();
	private static final BlockRenderer RENDERER = new BlockRenderer(VIEW);
	private static boolean renderSlab;
	
	public static void mfb_renderDoubleSlab(BlockView view, BaseBlock block, int x, int y, int z, ArsenicBlockRenderer renderer, CallbackInfoReturnable<Boolean> original, CallbackInfo info) {
		if (renderSlab) return;
		
		if (!(block instanceof MFBDoubleSlabBlock)) return;
		info.cancel();
		
		if (!(view.getBlockEntity(x, y, z) instanceof DoubleSlabBlockEntity entity)) {
			original.setReturnValue(false);
			return;
		}
		
		renderSlab = true;
		BlockState state;
		if (entity.bottomSlab != null) {
			state = entity.bottomSlab.getDefaultState();
			VIEW.setData(view, x, y, z, state);
			original.setReturnValue(false);
			renderer.renderWorld(state.getBlock(), x, y, z, original);
			if (!original.getReturnValue()) {
				RENDERER.render(state.getBlock(), x, y, z);
			}
			System.out.println("Render bottom " + state + " " + original.getReturnValue());
		}
		if (entity.topSlab != null) {
			state = entity.topSlab.getDefaultState().with(VBEBlockProperties.DIRECTION, Direction.UP);
			VIEW.setData(view, x, y, z, state);
			original.setReturnValue(false);
			renderer.renderWorld(state.getBlock(), x, y, z, original);
			if (!original.getReturnValue()) {
				RENDERER.render(state.getBlock(), x, y, z);
			}
			System.out.println("Render top " + state + " " + original.getReturnValue());
		}
		renderSlab = false;
		original.setReturnValue(true);
	}
	
	public static BlockView getView(BlockRendererAccessor accessor, Operation<BlockView> original) {
		return renderSlab ? VIEW : original.call(accessor);
	}
}
