package paulevs.mfb.container;

import net.minecraft.container.BaseContainer;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.inventory.BaseInventory;
import net.minecraft.item.ItemStack;

public class SimpleInventory implements BaseInventory {
	private final BaseContainer container;
	private final ItemStack[] items;
	
	public SimpleInventory(ItemStack[] items, BaseContainer container) {
		this.container = container;
		this.items = items;
	}
	
	public SimpleInventory(int size, BaseContainer container) {
		this(new ItemStack[size], container);
	}
	
	@Override
	public int getInventorySize() {
		return items.length;
	}
	
	@Override
	public ItemStack getItem(int index) {
		return items[index];
	}
	
	@Override
	public ItemStack takeItem(int index, int count) {
		if (items[index] != null) {
			if (items[index].count <= count) {
				ItemStack itemStack = items[index];
				items[index] = null;
				container.onContentsChanged(this);
				return itemStack;
			}
			ItemStack itemStack = items[index].split(count);
			if (items[index].count == 0) {
				items[index] = null;
				container.onContentsChanged(this);
			}
			return itemStack;
		}
		return null;
	}
	
	@Override
	public void setItem(int index, ItemStack stack) {
		items[index] = stack;
		container.onContentsChanged(this);
	}
	
	@Override
	public String getInventoryName() {
		return "Simple";
	}
	
	@Override
	public int getMaxStackSize() {
		return 64;
	}
	
	@Override
	public void markDirty() {}
	
	@Override
	public boolean canPlayerUse(PlayerBase arg) {
		return true;
	}
}
