package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.template.block.TemplateStairsBlock;
import net.modificationstation.stationapi.api.util.Identifier;

public class MFBStairsBlock extends TemplateStairsBlock {
	private final Block source;
	private final byte meta;
	
	public MFBStairsBlock(Identifier id, Block source, byte meta) {
		super(id, source);
		setLightOpacity(LIGHT_OPACITY[source.id]);
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setSounds(source.sounds);
		this.source = source;
		this.meta = meta;
		setTranslationKey(id.toString());
		NO_AMBIENT_OCCLUSION[this.id] = true;
	}
	
	@Override
	public int getTexture(BlockView view, int x, int y, int z, int side) {
		return getTexture(side);
	}
	
	@Override
	public int getTexture(int side, int meta) {
		return getTexture(side);
	}
	
	@Override
	public int getTexture(int side) {
		return source.getTexture(wrapSide(side), this.meta);
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
	
	private int wrapSide(int side) {
		return Math.min(side, 2);
	}
}
