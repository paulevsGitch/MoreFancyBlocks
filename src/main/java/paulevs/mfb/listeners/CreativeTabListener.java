package paulevs.mfb.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.ItemStack;
import paulevs.bhcreative.api.SimpleTab;
import paulevs.bhcreative.registry.TabRegistryEvent;
import paulevs.mfb.MFB;
import paulevs.mfb.block.MFBBlocks;
import paulevs.mfb.item.MFBItems;

public class CreativeTabListener {
	@EventListener
	public void registerTab(TabRegistryEvent event) {
		MFB.LOGGER.info("Adding MFB tab");
		SimpleTab tab = new SimpleTab(MFB.id("creative_tab"), new ItemStack(MFBBlocks.WOOD_SAW));
		event.register(tab);
		MFBBlocks.BLOCKS_WITH_ITEMS.forEach(block -> tab.addItem(new ItemStack(block)));
		MFBItems.ITEMS.forEach(block -> tab.addItem(new ItemStack(block)));
	}
}
