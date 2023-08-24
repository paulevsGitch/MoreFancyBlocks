package paulevs.mfb.block.blockentity;

import net.minecraft.block.BaseBlock;
import net.minecraft.block.entity.BaseBlockEntity;
import net.minecraft.util.io.CompoundTag;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.api.util.math.Direction.AxisDirection;
import paulevs.vbe.block.VBEBlockProperties;

public class DoubleSlabBlockEntity extends BaseBlockEntity {
	public BlockState bottomSlab;
	public BlockState topSlab;
	
	@Override
	public void readIdentifyingData(CompoundTag tag) {
		super.readIdentifyingData(tag);
		String id = tag.getString("BottomSlab");
		Axis axis = level.getBlockState(x, y, z).get(VBEBlockProperties.AXIS);
		if (id != null) {
			BaseBlock block = BlockRegistry.INSTANCE.get(Identifier.of(id));
			if (block != null) {
				Direction dir = Direction.from(axis, AxisDirection.NEGATIVE);
				bottomSlab = block.getDefaultState().with(VBEBlockProperties.DIRECTION, dir);
			}
		}
		id = tag.getString("TopSlab");
		if (id != null) {
			BaseBlock block = BlockRegistry.INSTANCE.get(Identifier.of(id));
			if (block != null) {
				Direction dir = Direction.from(axis, AxisDirection.POSITIVE);
				topSlab = block.getDefaultState().with(VBEBlockProperties.DIRECTION, dir);
			}
		}
	}
	
	@Override
	public void writeIdentifyingData(CompoundTag tag) {
		super.writeIdentifyingData(tag);
		if (bottomSlab != null) {
			Identifier id = BlockRegistry.INSTANCE.getId(bottomSlab.getBlock());
			if (id != null) {
				tag.put("BottomSlab", id.toString());
			}
		}
		if (topSlab != null) {
			Identifier id = BlockRegistry.INSTANCE.getId(topSlab.getBlock());
			if (id != null) {
				tag.put("TopSlab", id.toString());
			}
		}
	}
}
