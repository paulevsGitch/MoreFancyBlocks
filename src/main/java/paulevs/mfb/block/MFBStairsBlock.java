package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.block.TemplateStairs;

public class MFBStairsBlock extends TemplateStairs {
	private final BaseBlock source;
	private final byte meta;
	
	public MFBStairsBlock(Identifier id, BaseBlock source, byte meta) {
		super(id, source);
		setLightOpacity(LIGHT_OPACITY[source.id]);
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setSounds(source.sounds);
		this.source = source;
		this.meta = meta;
		setTranslationKey(id.toString());
	}
	
	@Override
	public int getTextureForSide(BlockView view, int x, int y, int z, int side) {
		return getTextureForSide(side);
	}
	
	@Override
	public int getTextureForSide(int side, int meta) {
		return getTextureForSide(side);
	}
	
	@Override
	public int getTextureForSide(int side) {
		return source.getTextureForSide(wrapSide(side), this.meta);
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
