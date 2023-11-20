package paulevs.mfb.block;

import net.minecraft.block.material.Material;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitType;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.block.VBEFullSlabBlock;

public class MFBDoubleSlabBlock extends VBEFullSlabBlock {
	private static BlockState blockState;
	private static float delta;
	
	public MFBDoubleSlabBlock(Identifier id) {
		super(id, Material.STONE);
		setHardness(1F);
		setTranslationKey(id.toString());
		setHalfBlock(VBEBlocks.OAK_SLAB_HALF);
	}
	
	@Override
	public void onBlockPlaced(Level level, int x, int y, int z) {
		super.onBlockPlaced(level, x, y, z);
		level.setBlockEntity(x, y, z, new DoubleSlabBlockEntity());
	}
	
	@Override
	public void onBlockRemoved(Level level, int x, int y, int z) {
		if (!blockState.isOf(this) || hit == null || hit.type != HitType.BLOCK) return;
		if (!(level.getBlockEntity(x, y, z) instanceof DoubleSlabBlockEntity entity)) return;
		BlockState state = delta > 0.5F ? entity.bottomSlab : entity.topSlab;
		level.setBlockState(x, y, z, state);
		level.removeBlockEntity(x, y, z);
	}
	
	@Override
	public void beforeBlockRemoved(Level level, int x, int y, int z) {
		super.beforeBlockRemoved(level, x, y, z);
		blockState = level.getBlockState(x, y, z);
		Axis axis = blockState.get(VBEBlockProperties.AXIS);
		switch (axis) {
			case X -> delta = (float) (hit.pos.x - hit.x);
			case Y -> delta = (float) (hit.pos.y - hit.y);
			case Z -> delta = (float) (hit.pos.z - hit.z);
		}
		if (!(level.getBlockEntity(x, y, z) instanceof DoubleSlabBlockEntity entity)) return;
		BlockState broken = delta < 0.5F ? entity.bottomSlab : entity.topSlab;
		this.texture = broken.getBlock().texture;
		this.sounds = broken.getBlock().sounds;
	}
}
