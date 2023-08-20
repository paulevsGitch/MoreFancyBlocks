package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.registry.Identifier;
import paulevs.vbe.block.VBEFullSlabBlock;

public class MFBFullSlabBlock extends VBEFullSlabBlock {
	private final BaseBlock source;
	private final byte meta;
	
	public MFBFullSlabBlock(Identifier id, BaseBlock source, byte meta) {
		super(id, source);
		this.source = source;
		this.meta = meta;
		setLightOpacity(LIGHT_OPACITY[source.id]);
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setSounds(source.sounds);
	}
	
	@Override
	@Environment(value=EnvType.CLIENT)
	public boolean isSideRendered(BlockView view, int x, int y, int z, int side) {
		return source.isSideRendered(view, x, y, z, side);
	}
	
	@Override
	public boolean isFullOpaque() {
		if (source == null) return true;
		return source.isFullOpaque();
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
		return source.getTextureForSide(wrapSide(side), this.meta);
	}
	
	@Override
	public int getTextureForSide(int side, int meta) {
		return source.getTextureForSide(wrapSide(side), this.meta);
	}
	
	private int wrapSide(int side) {
		return Math.min(side, 2);
	}
}
