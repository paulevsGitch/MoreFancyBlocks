package paulevs.mfb.block.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.ListTag;
import net.minecraft.util.io.StringTag;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.impl.block.StationFlatteningBlockInternal;
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
		
		int px = index % 3;
		int py = (index / 3) % 3;
		int pz = index / 9;
		
		int x1 = Math.max(px - 1, 0);
		int y1 = Math.max(py - 1, 0);
		int z1 = Math.max(pz - 1, 0);
		
		int x2 = Math.min(px + 1, 2);
		int y2 = Math.min(py + 1, 2);
		int z2 = Math.min(pz + 1, 2);
		
		for (int z = z1; z <= z2; z++) {
			int iz = z * 9;
			for (int y = y1; y <= y2; y++) {
				int iyz = iz + y * 3;
				for (int x = x1; x <= x2; x++) {
					int ixyz = iyz + x;
					if (states[ixyz] != null) return false;
				}
			}
		}
		
		/*int d = index % 3;
		if (d > 0 && states[index - 1] != null) return false;
		if (d < 2 && states[index + 1] != null) return false;
		
		d = (index / 3) % 3;
		if (d > 0 && states[index - 3] != null) return false;
		if (d < 2 && states[index + 3] != null) return false;
		
		d = index / 9;
		if (d > 0 && states[index - 9] != null) return false;
		if (d < 2 && states[index + 9] != null) return false;*/
		
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
	
	public int getMaxLight() {
		int light = 0;
		for (BlockState state : states) {
			if (state == null) continue;
			StationFlatteningBlockInternal internal = (StationFlatteningBlockInternal) state.getBlock();
			light = Math.max(light, internal.stationapi_getLuminanceProvider().applyAsInt(state));
		}
		return light;
	}
}
