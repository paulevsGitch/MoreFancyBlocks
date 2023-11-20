package paulevs.mfb.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.container.Container;
import net.minecraft.container.slot.Slot;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.PlayerInventory;
import net.minecraft.item.ItemStack;
import paulevs.mfb.api.SawAPI;
import paulevs.mfb.block.blockentity.SawBlockEntity;
import paulevs.mfb.item.SawBladeItem;

import java.util.List;

public class SawContainer extends Container {
	private final SimpleInventory crafting;
	private final SimpleInventory preview = new SimpleInventory(24, this);
	private final Slot[] previewSlots = new Slot[25];
	private final PlayerInventory inventory;
	private final SawBlockEntity entity;
	
	private List<ItemStack> variants;
	private ItemStack selectedItem;
	private ItemStack lastItem;
	
	public byte sound;
	
	public SawContainer(PlayerInventory inventory, SawBlockEntity entity) {
		crafting = new SimpleInventory(entity.items, this);
		this.inventory = inventory;
		this.entity = entity;
		lastItem = crafting.getItem(0);
		
		addSlot(new Slot(crafting, 0, 18, 8));
		addSlot(new Slot(crafting, 1, 18, 31));
		addSlot(new Slot(crafting, 2, 18, 58));
		
		for (byte row = 0; row < 3; ++row) {
			for (byte column = 0; column < 9; ++column) {
				addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
			}
		}
		
		for (byte i = 0; i < 9; ++i) {
			addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
		
		for (byte row = 0; row < 4; ++row) {
			for (byte column = 0; column < 6; ++column) {
				int index = column + row * 6;
				previewSlots[index] = new Slot(preview, index, column * 18 + 62, row * 18 + 8);
				addSlot(previewSlots[index]);
			}
		}
		
		onContentsChanged(null);
		updateVariants();
		if (variants != null && entity.selectedSlot > -1 && entity.selectedSlot < variants.size()) {
			selectedItem = variants.get(entity.selectedSlot);
		}
		getSlot(2).setStack(null);
		getSlot(2).markDirty();
	}
	
	@Override
	public void onContentsChanged(Inventory inventory) {
		ItemStack output = crafting.getItem(2);
		if (output == null && selectedItem != null) {
			getSlot(2).setStack(selectedItem.copy());
			getSlot(2).markDirty();
		}
		if (output != null && selectedItem != null && !output.isDamageAndIDIdentical(selectedItem)) {
			getSlot(2).setStack(selectedItem.copy());
			getSlot(2).markDirty();
		}
		
		ItemStack inputItem = crafting.getItem(0);
		if (lastItem == inputItem) return;
		if (lastItem != null && inputItem != null) {
			if (lastItem.isDamageAndIDIdentical(inputItem)) return;
		}
		lastItem = inputItem;
		selectedItem = null;
		entity.selectedSlot = -1;
		
		updateVariants();
		
		getSlot(2).setStack(null);
		getSlot(2).markDirty();
	}
	
	private void updateVariants() {
		ItemStack saw = crafting.getItem(1);
		if (saw == null || !(saw.getType() instanceof SawBladeItem)) {
			variants = null;
		}
		else variants = SawAPI.getResults(lastItem);
		if (variants == null) {
			for (int i = 0; i < preview.getInventorySize(); i++) {
				if (preview.getItem(i) == null) break;
				preview.setItem(i, null);
				previewSlots[i].markDirty();
				selectedItem = null;
			}
		}
		else {
			int lastItem = Math.min(24, variants.size());
			for (int i = 0; i < lastItem; i++) {
				preview.setItem(i, variants.get(i));
				previewSlots[i].markDirty();
			}
		}
	}
	
	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}
	
	@Override
	public ItemStack clickSlot(int index, int button, boolean shift, PlayerEntity player) {
		if (index == 1) {
			ItemStack playerItem = inventory.getCursorItem();
			if (playerItem == null || playerItem.getType() instanceof SawBladeItem) {
				ItemStack result = super.clickSlot(index, button, shift, player);
				lastItem = null;
				onContentsChanged(null);
				return result;
			}
			return null;
		}
		
		if (index == 2) {
			if (selectedItem == null || getSlot(1).getItem() == null) return null;
			ItemStack playerItem = inventory.getCursorItem();
			
			if (playerItem == null) {
				int tries = shift ? countTries(0, getSlot(0).getItem().count) : 1;
				ItemStack output = selectedItem.copy();
				output.count = tries * selectedItem.count;
				inventory.setCursorItem(output);
				getSlot(0).getItem().count -= tries;
				getSlot(0).markDirty();
				getSlot(1).getItem().applyDamage(tries, null);
				getSlot(1).markDirty();
				sound = 1;
			}
			else if (playerItem.isDamageAndIDIdentical(selectedItem) && playerItem.count + selectedItem.count <= selectedItem.getMaxStackSize()) {
				int tries = shift ? countTries(playerItem.count, getSlot(0).getItem().count) : 1;
				playerItem.count += tries * selectedItem.count;
				getSlot(0).getItem().count -= tries;
				getSlot(0).markDirty();
				getSlot(1).getItem().applyDamage(tries, null);
				getSlot(1).markDirty();
				sound = 1;
			}
			
			boolean update = false;
			
			if (getSlot(0).getItem().count < 1) {
				getSlot(0).setStack(null);
				getSlot(0).markDirty();
				update = true;
			}
			
			if (getSlot(1).getItem().count < 1) {
				getSlot(1).setStack(null);
				getSlot(1).markDirty();
				getSlot(2).setStack(null);
				getSlot(2).markDirty();
				sound = 2;
				lastItem = null;
				update = true;
			}
			
			if (update) onContentsChanged(null);
			
			playSound();
			return null;
		}
		
		if (index > 38) {
			if (variants == null || (index - 39) >= variants.size()) return null;
			entity.selectedSlot = index - 39;
			selectedItem = variants.get(entity.selectedSlot);
			getSlot(2).setStack(selectedItem.copy());
			getSlot(2).markDirty();
			sound = 3;
			playSound();
			return null;
		}
		
		return super.clickSlot(index, button, shift, player);
	}
	
	@Override
	public ItemStack transferSlot(int index) {
		Slot slot = getSlot(index);
		ItemStack stack = slot.getItem();
		
		if (stack == null) return null;
		
		if (index < 2) {
			if (inventory.addStack(stack)) {
				slot.setStack(null);
			}
			slot.markDirty();
		}
		
		if (index == 2) {
			stack.count *= crafting.getItem(0).count;
			if (inventory.addStack(stack)) {
				slot = getSlot(0);
				slot.setStack(null);
			}
			slot.markDirty();
		}
		
		return null;
	}
	
	@Override
	public void onClosed(PlayerEntity player) {
		if (player.level.isRemote) return;
		PlayerInventory inventory = player.inventory;
		addOrDrop(player, inventory.getCursorItem());
		inventory.setCursorItem(null);
		addOrDrop(player, this.crafting.getItem(0));
	}
	
	public int getSelectedSlot() {
		return entity.selectedSlot;
	}
	
	private void addOrDrop(PlayerEntity player, ItemStack stack) {
		if (stack == null) return;
		if (!player.inventory.addStack(stack)) {
			player.dropItem(stack);
		}
	}
	
	private int countTries(int currentCount, int maxCount) {
		int count = Math.min(selectedItem.getMaxStackSize() - currentCount, maxCount * selectedItem.count);
		count = count / selectedItem.count;
		ItemStack saw = getSlot(1).getItem();
		count = Math.min(count, saw.getDurability() - saw.getDamage() + 1);
		return count;
	}
	
	private void playSound() {
		if (sound == 0 || FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
		playClientSound();
		sound = 0;
	}
	
	@Environment(EnvType.CLIENT)
	private void playClientSound() {
		@SuppressWarnings("deprecation")
		Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
		switch (sound) {
			case 1 -> minecraft.soundHelper.playSound("mfb:saw_normal", 1, 1);
			case 2 -> minecraft.soundHelper.playSound("mfb:saw_break", 1, 1);
			case 3 -> minecraft.soundHelper.playSound("random.click", 1, 1);
		}
	}
}
