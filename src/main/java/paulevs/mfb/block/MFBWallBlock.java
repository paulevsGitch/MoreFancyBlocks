package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.template.block.TemplateFenceBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.vbe.block.FenceConnector;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlockTags;
import paulevs.vbe.block.VBEHalfSlabBlock;

import java.util.ArrayList;

public class MFBWallBlock extends TemplateFenceBlock implements FenceConnector {
	private static final boolean[] CAN_CONNECT = new boolean[4];
	private final Block source;
	private final byte meta;
	
	public int sideTexture;
	public int topTexture;
	
	public MFBWallBlock(Identifier id, Block source, byte meta) {
		super(id, source.texture);
		this.source = source;
		this.meta = meta;
		setLightOpacity(Math.min(LIGHT_OPACITY[source.id], LIGHT_OPACITY[this.id]));
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setTranslationKey(id.toString());
		setSounds(source.sounds);
		setHardness(source.getHardness());
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
		if (side < 2) return topTexture == 0 ? source.getTexture(side, this.meta) : topTexture;
		return sideTexture == 0 ? source.getTexture(2, this.meta) : sideTexture;
	}
	
	@Override
	public int getTexture(int side, int meta) {
		return getTexture(side);
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
		boolean[] connections = getConnections(level, x, y, z);
		
		for (byte i = 0; i < 4; i++) {
			Direction dir = Direction.fromHorizontal(i);
			if (connections[i]) {
				float minX = 0.3125F;
				float minZ = 0.3125F;
				float maxX = 0.6875F;
				float maxZ = 0.6875F;
				
				if (dir.getOffsetX() < 0) { minX = 0; maxX = 0.5F; }
				if (dir.getOffsetX() > 0) { maxX = 1; minX = 0.5F; }
				if (dir.getOffsetZ() < 0) { minZ = 0; maxZ = 0.5F; }
				if (dir.getOffsetZ() > 0) { maxZ = 1; minZ = 0.5F; }
				
				this.setBoundingBox(minX, 0.0F, minZ, maxX, 1.5F, maxZ);
				super.doesBoxCollide(level, x, y, z, box, list);
			}
		}
		
		if (hasPost(level, x, y, z, connections)) {
			this.setBoundingBox(0.25F, 0.0F, 0.25F, 0.75F, 1.5F, 0.75F);
			super.doesBoxCollide(level, x, y, z, box, list);
		}
		
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public void updateBoundingBox(BlockView blockView, int x, int y, int z) {
		if (blockView instanceof Level level) {
			updateBox(level, x, y, z);
		}
	}
	
	private void updateBox(Level level, int x, int y, int z) {
		float minX = 0.3125F;
		float minZ = 0.3125F;
		float maxX = 0.6875F;
		float maxZ = 0.6875F;
		float maxY = 0.8125F;
		
		boolean[] connections = getConnections(level, x, y, z);
		
		for (byte i = 0; i < 4; i++) {
			Direction dir = Direction.fromHorizontal(i);
			if (connections[i]) {
				if (dir.getOffsetX() < 0) minX = 0;
				if (dir.getOffsetX() > 0) maxX = 1;
				if (dir.getOffsetZ() < 0) minZ = 0;
				if (dir.getOffsetZ() > 0) maxZ = 1;
			}
		}
		
		if (hasPost(level, x, y, z, connections)) {
			maxY = 1;
			minX = Math.min(minX, 0.25F);
			minZ = Math.min(minZ, 0.25F);
			maxX = Math.max(maxX, 0.75F);
			maxZ = Math.max(maxZ, 0.75F);
		}
		else if (extendWalls(level, x, y, z)) maxY = 1;
		
		setBoundingBox(minX, 0.0F, minZ, maxX, maxY, maxZ);
	}
	
	@Override
	public boolean vbe_canConnect(BlockState state, Direction face) {
		if (state.isIn(VBEBlockTags.FENCE_CONNECT)) return true;
		Block block = state.getBlock();
		if (block instanceof VBEHalfSlabBlock) {
			return state.get(VBEBlockProperties.DIRECTION).getOpposite() == face;
		}
		return block instanceof FenceBlock || (block.isFullOpaque() && block.isFullCube());
	}
	
	public boolean[] getConnections(BlockStateView level, int x, int y, int z) {
		for (byte i = 0; i < 4; i++) {
			Direction dir = Direction.fromHorizontal(i);
			BlockState side = level.getBlockState(x + dir.getOffsetX(), y, z + dir.getOffsetZ());
			CAN_CONNECT[i] = vbe_canConnect(side, dir);
			if (CAN_CONNECT[i]) {
				if (dir.getOffsetX() < 0) minX = 0;
				if (dir.getOffsetX() > 0) maxX = 1;
				if (dir.getOffsetZ() < 0) minZ = 0;
				if (dir.getOffsetZ() > 0) maxZ = 1;
			}
		}
		return CAN_CONNECT;
	}
	
	public boolean hasPost(BlockStateView level, int x, int y, int z, boolean[] connections) {
		if (postByConnections(connections)) return true;
		while (true) {
			BlockState state = level.getBlockState(x, ++y, z);
			Block block = state.getBlock();
			if (block instanceof TorchBlock) return true;
			if (block instanceof MFBOctablock) return state.get(MFBBlockProperties.OCTABLOCK) == 10;
			if (block instanceof MFBWallBlock wallBlock) {
				connections = wallBlock.getConnections(level, x, y, z);
				if (postByConnections(connections)) return true;
				continue;
			}
			break;
		}
		return false;
	}
	
	public boolean extendWalls(BlockStateView view, int x, int y, int z) {
		for (byte i = 0; i < 4; i++) {
			Direction dir = Direction.fromHorizontal(i);
			BlockState side = view.getBlockState(x + dir.getOffsetX(), y, z + dir.getOffsetZ());
			Block block = side.getBlock();
			if (block instanceof FenceBlock && !(block instanceof MFBWallBlock)) return true;
		}
		
		BlockState state = view.getBlockState(x, y + 1, z);
		if (state.isAir()) return false;
		Block block = state.getBlock();
		
		if (block.isFullCube() && block.material.blocksMovement()) return true;
		if (block instanceof MFBWallBlock) return true;
		if (block instanceof VBEHalfSlabBlock && state.get(VBEBlockProperties.DIRECTION) == Direction.DOWN) return true;
		if (block instanceof MFBPanelBlock && state.get(VBEBlockProperties.DIRECTION) == Direction.DOWN) return true;
		
		if (view instanceof Level level) {
			Box shape = block.getCollisionShape(level, x, y + 1, z);
			if (shape == null) return false;
			else shape.move(-x, -y - 1, -z);
			return shape.minY < 0.01F && shape.minX < 0.01F && shape.minZ < 0.01F && shape.maxX > 0.99F && shape.maxZ > 0.99F;
		}
		
		return false;
	}
	
	private boolean postByConnections(boolean[] connections) {
		byte count = 0;
		for (byte i = 0; i < 4; i++) {
			if (connections[i]) count++;
		}
		return count != 2 || connections[0] != connections[2];
	}
}
