package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.util.Identifier;
import paulevs.vbe.block.VBEHalfSlabBlock;

public class MFBHalfSlabBlock extends VBEHalfSlabBlock {
	private final Block source;
	private final byte meta;
	
	public MFBHalfSlabBlock(Identifier id, Block source, byte meta) {
		super(id, source);
		this.source = source;
		this.meta = meta;
		setLightOpacity(Math.min(LIGHT_OPACITY[source.id], LIGHT_OPACITY[this.id]));
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setSounds(source.sounds);
		NO_AMBIENT_OCCLUSION[this.id] = true;
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
