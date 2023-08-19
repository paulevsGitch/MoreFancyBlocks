package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.vbe.block.VBEHalfSlabBlock;

public class MFBHalfSlabBlock extends VBEHalfSlabBlock {
	private final BaseBlock source;
	private final byte meta;
	
	public MFBHalfSlabBlock(Identifier id, BaseBlock source, byte meta) {
		super(id, source);
		this.source = source;
		this.meta = meta;
		this.setLightOpacity(LIGHT_OPACITY[source.id]);
	}
	
	@Override
	@Environment(value= EnvType.CLIENT)
	public boolean isSideRendered(BlockView view, int x, int y, int z, int side) {
		boolean render = super.isSideRendered(view, x, y, z, side);
		if (!render && view instanceof BlockStateView blockStateView) {
			BlockState state = blockStateView.getBlockState(x, y, z);
			render = !state.isOf(this);
		}
		return render;
	}
	
	@Override
	public int getColorMultiplier(BlockView view, int x, int y, int z) {
		return source.getColorMultiplier(view, x, y, z);
	}
	
	@Override
	public int getBaseColor(int meta) {
		return source.getBaseColor(meta);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getRenderPass() {
		return source.getRenderPass();
	}
	
	@Override
	public int getTextureForSide(int side) {
		return source.getTextureForSide(side, this.meta);
	}
	
	@Override
	public int getTextureForSide(int side, int meta) {
		return source.getTextureForSide(side, this.meta);
	}
	
	@Override
	public String getTranslatedName() {
		return "Slab!";
	}
}
