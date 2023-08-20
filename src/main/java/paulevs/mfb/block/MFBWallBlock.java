package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.block.TemplateFence;
import net.modificationstation.stationapi.api.util.math.Direction;
import paulevs.vbe.block.FenceConnector;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockTags;
import paulevs.vbe.block.VBEHalfSlabBlock;

import java.util.ArrayList;

public class MFBWallBlock extends TemplateFence implements FenceConnector {
	private final boolean[] canConnect = new boolean[4];
	private final BaseBlock source;
	private final byte meta;
	
	public int sideTexture;
	public int topTexture;
	
	public MFBWallBlock(Identifier id, BaseBlock source, byte meta) {
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
	
	@Override
	public Box getCollisionShape(Level level, int x, int y, int z) {
		return Box.createAndCache(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
	}
	
	@Override
	@Environment(value= EnvType.CLIENT)
	public Box getOutlineShape(Level level, int x, int y, int z) {
		updateBox(level, x, y, z);
		return Box.createAndCache(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
	}
	
	@Override
	public void doesBoxCollide(Level level, int x, int y, int z, Box box, ArrayList list) {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		super.doesBoxCollide(level, x, y, z, box, list);
	}
	
	private void updateBox(Level level, int x, int y, int z) {
		float minX = 0.3125F;
		float minZ = 0.3125F;
		float maxX = 0.6875F;
		float maxZ = 0.6875F;
		float maxY = 0.8125F;
		
		byte count = 0;
		for (byte i = 0; i < 4; i++) {
			Direction dir = Direction.fromHorizontal(i);
			BlockState side = level.getBlockState(x + dir.getOffsetX(), y, z + dir.getOffsetZ());
			canConnect[i] = vbe_canConnect(side, dir);
			if (canConnect[i]) {
				if (dir.getOffsetX() < 0) minX = 0;
				if (dir.getOffsetX() > 0) maxX = 1;
				if (dir.getOffsetZ() < 0) minZ = 0;
				if (dir.getOffsetZ() > 0) maxZ = 1;
				count++;
			}
		}
		
		if (count < 2 || count == 3 || (count == 2 && canConnect[0] != canConnect[2])) {
			maxY = 1;
			minX = Math.min(minX, 0.25F);
			minZ = Math.min(minZ, 0.25F);
			maxX = Math.max(maxX, 0.75F);
			maxZ = Math.max(maxZ, 0.75F);
		}
		
		setBoundingBox(minX, 0.0F, minZ, maxX, maxY, maxZ);
	}
	
	@Override
	public boolean vbe_canConnect(BlockState state, Direction face) {
		if (state.isIn(VBEBlockTags.FENCE_CONNECT)) return true;
		BaseBlock block = state.getBlock();
		if (block instanceof VBEHalfSlabBlock) {
			return state.get(VBEBlockProperties.DIRECTION).getOpposite() == face;
		}
		return block instanceof FenceBlock || (block.isFullOpaque() && block.isFullCube());
	}
}
