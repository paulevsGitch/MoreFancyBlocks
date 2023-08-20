package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.block.TemplateFence;

public class MFBFenceBlock extends TemplateFence {
	private final BaseBlock source;
	private final byte meta;
	
	public int sideTexture;
	public int topTexture;
	
	public MFBFenceBlock(Identifier id, BaseBlock source, byte meta) {
		super(id, source.texture);
		this.source = source;
		this.meta = meta;
		setLightOpacity(Math.min(LIGHT_OPACITY[source.id], LIGHT_OPACITY[this.id]));
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setTranslationKey(id.toString());
		setSounds(source.sounds);
		setHardness(source.getHardness());
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
		if (side < 2) return topTexture == 0 ? source.getTextureForSide(side, this.meta) : topTexture;
		return sideTexture == 0 ? source.getTextureForSide(2, this.meta) : sideTexture;
	}
	
	@Override
	public int getTextureForSide(int side, int meta) {
		return getTextureForSide(side);
	}
}
