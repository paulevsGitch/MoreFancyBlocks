package paulevs.mfb.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicBlockRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.BlockRenderManagerAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.mfb.client.render.CustomBlockRenderer;

@Mixin(value = ArsenicBlockRenderer.class, remap = false)
public abstract class ArsenicBlockRendererMixin {
	@Shadow @Final private BlockRenderManagerAccessor blockRendererAccessor;
	
	@Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
	private void mfb_renderDoubleSlab(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> original, CallbackInfo info) {
		ArsenicBlockRenderer renderer = ArsenicBlockRenderer.class.cast(this);
		CustomBlockRenderer.render(blockRendererAccessor, block, x, y, z, renderer, original, info);
	}
	
	@WrapOperation(method = "renderWorld", at = @At(
		value = "INVOKE",
		target = "Lnet/modificationstation/stationapi/mixin/arsenic/client/BlockRenderManagerAccessor;getBlockView()Lnet/minecraft/level/BlockView;"
	))
	private BlockView mfb_getDefaultState(BlockRenderManagerAccessor accessor, Operation<BlockView> original) {
		return CustomBlockRenderer.getView(accessor, original);
	}
}
