package paulevs.mfb.block;

import net.minecraft.block.material.Material;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitType;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.util.math.Direction.AxisDirection;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.block.VBEFullSlabBlock;
import paulevs.vbe.block.VBEHalfSlabBlock;
import paulevs.vbe.utils.LevelUtil;

public class MFBDoubleSlabBlock extends VBEFullSlabBlock {
	private static BlockState blockState;
	
	public MFBDoubleSlabBlock(Identifier id) {
		super(id, Material.STONE);
		setHardness(1F);
		setTranslationKey(id.toString());
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
		Axis axis = blockState.get(VBEBlockProperties.AXIS);
		float delta = 0;
		switch (axis) {
			case X -> delta = (float) (hit.pos.x - hit.x);
			case Y -> delta = (float) (hit.pos.y - hit.y);
			case Z -> delta = (float) (hit.pos.z - hit.z);
		}
		Direction facing = Direction.from(axis, delta > 0.5F ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
		VBEHalfSlabBlock block = delta > 0.5F ? entity.bottomSlab : entity.topSlab;
		level.setBlockState(x, y, z, block.getDefaultState().with(VBEBlockProperties.DIRECTION, facing));
		level.removeBlockEntity(x, y, z);
	}
	
	@Override
	public void beforeBlockRemoved(Level level, int x, int y, int z) {
		super.beforeBlockRemoved(level, x, y, z);
		blockState = level.getBlockState(x, y, z);
	}
}
