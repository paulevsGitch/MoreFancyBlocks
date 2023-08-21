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
		MFBBlocks.BLOCKS_WITH_ITEMS.forEach(block -> mainTab.addItem(new ItemStack(block.asItem())));
		MFBItems.ITEMS.forEach(block -> mainTab.addItem(new ItemStack(block)));
		
		final CycleCreativeTab stairsTab = new CycleCreativeTab(MFB.id("stairs_tab"));
		event.register(stairsTab);
		MFBBlocks.STAIRS.forEach(block -> stairsTab.addItem(new ItemStack(block.asItem())));
		
		final CycleCreativeTab slabsTab = new CycleCreativeTab(MFB.id("slabs_tab"));
		event.register(slabsTab);
		MFBBlocks.SLABS.forEach(block -> slabsTab.addItem(new ItemStack(block.asItem())));
		
		final CycleCreativeTab panelsTab = new CycleCreativeTab(MFB.id("panels_tab"));
		event.register(panelsTab);
		MFBBlocks.PANELS.forEach(block -> panelsTab.addItem(new ItemStack(block.asItem())));
		
		final CycleCreativeTab fencesTab = new CycleCreativeTab(MFB.id("fences_tab"));
		event.register(fencesTab);
		MFBBlocks.FENCES.forEach(block -> fencesTab.addItem(new ItemStack(block.asItem())));
		
		final CycleCreativeTab wallsTab = new CycleCreativeTab(MFB.id("walls_tab"));
		event.register(wallsTab);
		MFBBlocks.WALLS.forEach(block -> wallsTab.addItem(new ItemStack(block.asItem())));
		
		final CycleCreativeTab octablocksTab = new CycleCreativeTab(MFB.id("octablocks_tab"));
		event.register(octablocksTab);
		MFBBlocks.OCTABLOCKS.forEach(block -> octablocksTab.addItem(new ItemStack(block.asItem())));
	}
}
