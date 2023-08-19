package paulevs.mfb.block;

import net.minecraft.block.entity.BaseBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.ListTag;

public class SawBlockEntity extends BaseBlockEntity {
	public final ItemStack[] items = new ItemStack[3];
	public int selectedSlot = -1;
	
	@Override
	public void readIdentifyingData(CompoundTag tag) {
		super.readIdentifyingData(tag);
		ListTag listTag = tag.getListTag("Items");
		for (int i = 0; i < listTag.size(); ++i) {
			CompoundTag itemTag = (CompoundTag) listTag.get(i);
			int slot = itemTag.getByte("Slot");
			items[slot] = new ItemStack(itemTag);
		}
		selectedSlot = tag.getInt("SelectedSlot");
	}
	
	@Override
	public void writeIdentifyingData(CompoundTag tag) {
		super.writeIdentifyingData(tag);
		ListTag listTag = new ListTag();
		for (int index = 0; index < items.length; index++) {
			if (items[index] == null || items[index].count < 1) continue;
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.put("Slot", (byte) index);
			items[index].toTag(compoundTag);
			listTag.add(compoundTag);
		}
		tag.put("Items", listTag);
		tag.put("SelectedSlot", selectedSlot);
	}
}
