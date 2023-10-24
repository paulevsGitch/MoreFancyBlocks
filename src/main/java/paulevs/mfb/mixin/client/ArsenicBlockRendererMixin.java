package paulevs.mfb.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BaseBlock;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicBlockRenderer;
import net.modificationstation.stationapi.mixin.arsenic.client.BlockRendererAccessor;
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
	@Shadow @Final private BlockRendererAccessor blockRendererAccessor;
	
	@Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
	private void mfb_renderDoubleSlab(BaseBlock block, int x, int y, int z, CallbackInfoReturnable<Boolean> original, CallbackInfo info) {
		ArsenicBlockRenderer renderer = ArsenicBlockRenderer.class.cast(this);
		CustomBlockRenderer.mfb_renderDoubleSlab(blockRendererAccessor, block, x, y, z, renderer, original, info);
	}
	
	@WrapOperation(method = "renderWorld", at = @At(
		value = "INVOKE",
		target = "Lnet/modificationstation/stationapi/mixin/arsenic/client/BlockRendererAccessor;getBlockView()Lnet/minecraft/level/BlockView;"
	))
	private BlockView mfb_getDefaultState(BlockRendererAccessor accessor, Operation<BlockView> original) {
		return CustomBlockRenderer.getView(accessor, original);
	}
}
