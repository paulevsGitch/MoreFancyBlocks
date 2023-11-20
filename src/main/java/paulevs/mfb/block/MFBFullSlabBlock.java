package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.util.Identifier;
import paulevs.vbe.block.VBEFullSlabBlock;

public class MFBFullSlabBlock extends VBEFullSlabBlock {
	private final Block source;
	private final byte meta;
	
	public MFBFullSlabBlock(Identifier id, Block source, byte meta) {
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
	public int getTexture(int side) {
		return source.getTexture(wrapSide(side), this.meta);
	}
	
	@Override
	public int getTexture(int side, int meta) {
		return source.getTexture(wrapSide(side), this.meta);
	}
	
	private int wrapSide(int side) {
		return Math.min(side, 2);
	}
}
