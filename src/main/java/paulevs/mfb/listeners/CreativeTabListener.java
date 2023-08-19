package paulevs.mfb.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.ItemStack;
import paulevs.bhcreative.api.SimpleTab;
import paulevs.bhcreative.registry.TabRegistryEvent;
import paulevs.mfb.MFB;
import paulevs.mfb.block.MFBBlocks;
import paulevs.mfb.item.MFBItems;
import paulevs.mfb.screen.CycleCreativeTab;

public class CreativeTabListener {
	@EventListener
	public void registerTab(TabRegistryEvent event) {
		MFB.LOGGER.info("Adding MFB tabs");
		
		final SimpleTab mainTab = new SimpleTab(MFB.id("main_tab"), new ItemStack(MFBBlocks.WOOD_SAW));
		event.register(mainTab);
		MFBItems.ITEMS.forEach(block -> mainTab.addItem(new ItemStack(block)));
		MFBBlocks.BLOCKS_WITH_ITEMS.forEach(block -> mainTab.addItem(new ItemStack(block)));
		
		final CycleCreativeTab slabsTab = new CycleCreativeTab(MFB.id("slabs_tab"));
		event.register(slabsTab);
		MFBBlocks.SLABS.forEach(block -> slabsTab.addItem(new ItemStack(block)));
	}
}
