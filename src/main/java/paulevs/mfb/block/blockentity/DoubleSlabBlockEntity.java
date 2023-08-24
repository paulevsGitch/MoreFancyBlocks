package paulevs.mfb.block.blockentity;

import net.minecraft.block.entity.BaseBlockEntity;
import net.minecraft.util.io.CompoundTag;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import paulevs.vbe.block.VBEHalfSlabBlock;

public class DoubleSlabBlockEntity extends BaseBlockEntity {
	public VBEHalfSlabBlock bottomSlab;
	public VBEHalfSlabBlock topSlab;
	
	@Override
	public void readIdentifyingData(CompoundTag tag) {
		super.readIdentifyingData(tag);
		String id = tag.getString("BottomSlab");
		if (id != null) {
			bottomSlab = (VBEHalfSlabBlock) BlockRegistry.INSTANCE.get(Identifier.of(id));
		}
		id = tag.getString("TopSlab");
		if (id != null) {
			topSlab = (VBEHalfSlabBlock) BlockRegistry.INSTANCE.get(Identifier.of(id));
		}
	}
	
	@Override
	public void writeIdentifyingData(CompoundTag tag) {
		super.writeIdentifyingData(tag);
		if (bottomSlab != null) {
			Identifier id = BlockRegistry.INSTANCE.getId(bottomSlab);
			if (id != null) {
				tag.put("BottomSlab", id.toString());
			}
		}
		if (topSlab != null) {
			Identifier id = BlockRegistry.INSTANCE.getId(topSlab);
			if (id != null) {
				tag.put("TopSlab", id.toString());
			}
		}
	}
}
