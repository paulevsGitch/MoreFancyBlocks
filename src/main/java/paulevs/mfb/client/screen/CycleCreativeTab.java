package paulevs.mfb.client.screen;

import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.Identifier;
import paulevs.bhcreative.api.CreativeTab;

import java.util.List;

public class CycleCreativeTab extends CreativeTab {
	public CycleCreativeTab(Identifier id) {
		super(id);
	}
	
	@Override
	public ItemStack getIcon() {
		List<ItemStack> items = getItems();
		int index = (int) (System.currentTimeMillis() / 1000) % items.size();
		return items.get(index);
	}
}
