package paulevs.mfb.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.block.BaseBlock;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.block.BlockEvent.BeforePlacedByItem;
import net.modificationstation.stationapi.api.event.registry.AfterBlockAndItemRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.event.resource.language.TranslationInvalidationEvent;
import net.modificationstation.stationapi.api.event.tileentity.TileEntityRegisterEvent;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.mixin.lang.TranslationStorageAccessor;
import paulevs.mfb.api.SawAPI;
import paulevs.mfb.block.MFBBlocks;
import paulevs.mfb.block.SawBlock;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;
import paulevs.mfb.block.blockentity.SawBlockEntity;
import paulevs.mfb.item.MFBItems;

import java.util.Properties;

public class CommonListener {
	@EventListener(priority = ListenerPriority.LOWEST)
	private void registerBLocks(BlockRegistryEvent event) {
		MFBBlocks.init();
	}
	
	@EventListener
	private void registerItems(ItemRegistryEvent event) {
		MFBItems.init();
	}
	
	@EventListener
	private void onTileEntityRegister(TileEntityRegisterEvent event) {
		event.register(SawBlockEntity.class, "mfb:saw");
		event.register(DoubleSlabBlockEntity.class, "mfb:double_slab");
	}
	
	@EventListener
	private void beforeItemPlace(BeforePlacedByItem event) {
		if (!(event.block instanceof SawBlock sawBlock)) return;
		Direction facing = Direction.fromRotation(event.player.yaw).rotateClockwise(Axis.Y);
		int x = event.x + facing.getOffsetX();
		int z = event.z + facing.getOffsetZ();
		if (!sawBlock.canPlaceAt(event.world, x, event.y, z)) {
			event.placeFunction = () -> false;
		}
	}
	
	@EventListener(priority = ListenerPriority.LOWEST)
	private void onRecipesRegister(AfterBlockAndItemRegisterEvent event) {
		SawAPI.loadPromisedRecipes();
		
		SawAPI.addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.STONE_SLAB, 2, 2));
		SawAPI.addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.WOODEN_PRESSURE_PLATE));
		SawAPI.addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.WOOD_STAIRS));
		SawAPI.addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.FENCE));
		
		SawAPI.addRecipe(new ItemStack(BaseBlock.LOG), new ItemStack(BaseBlock.WOOD, 6));
		
		SawAPI.addRecipe(new ItemStack(BaseBlock.COBBLESTONE), new ItemStack(BaseBlock.STONE_SLAB, 2, 3));
		SawAPI.addRecipe(new ItemStack(BaseBlock.COBBLESTONE), new ItemStack(BaseBlock.COBBLESTONE_STAIRS));
	}
	
	@EventListener(priority = ListenerPriority.LOWEST)
	private void onResourceReload(TranslationInvalidationEvent event) {
		Properties translations = ((TranslationStorageAccessor) TranslationStorage.getInstance()).getTranslations();
		MFBBlocks.TRANSLATIONS.forEach(triple -> {
			String name = translations.getProperty(triple.getMiddle(), triple.getMiddle());
			translations.put(triple.getLeft(), name + triple.getRight());
		});
	}
}
