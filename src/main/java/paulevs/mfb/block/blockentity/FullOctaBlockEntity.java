package paulevs.mfb.block.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.ListTag;
import net.minecraft.util.io.StringTag;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import paulevs.mfb.block.MFBBlockProperties;

public class FullOctaBlockEntity extends BlockEntity {
	public final BlockState[] states = new BlockState[27];
	
	@Override
	public void readIdentifyingData(CompoundTag tag) {
		super.readIdentifyingData(tag);
		ListTag statesTag = tag.getListTag("Blocks");
		for (byte i = 0; i < statesTag.size(); i++) {
			states[i] = null;
			String idString = ((StringTag) statesTag.get(i)).data;
			if (idString == null || idString.isEmpty()) continue;
			Block block = BlockRegistry.INSTANCE.get(Identifier.of(idString));
			if (block == null) continue;
			states[i] = block.getDefaultState().with(MFBBlockProperties.OCTABLOCK, (int) i);
		}
	}
	
	@Override
	public void writeIdentifyingData(CompoundTag tag) {
		super.writeIdentifyingData(tag);
		ListTag statesTag = new ListTag();
		tag.put("Blocks", statesTag);
		for (BlockState state : states) {
			String name = "";
			if (state != null) {
				Identifier id = BlockRegistry.INSTANCE.getId(state.getBlock());
				if (id != null) name = id.toString();
			}
			statesTag.add(new StringTag(name));
		}
	}
	
	public boolean setOctablock(BlockState state) {
		int index = state.get(MFBBlockProperties.OCTABLOCK);
		if (states[index] != null) return false;
		states[index] = state;
		return true;
	}
	
	public boolean needRemoval() {
		for (BlockState state : states) {
			if (state != null) return false;
		}
		return true;
	}
	
	public BlockState getLastState() {
		BlockState last = null;
		for (BlockState state : states) {
			if (state != null) {
				if (last != null) return null;
				last = state;
			}
		}
		return last;
	}
}
