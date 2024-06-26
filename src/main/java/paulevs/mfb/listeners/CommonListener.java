package paulevs.mfb.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.block.Block;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.block.BlockEvent.BeforePlacedByItem;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.AfterBlockAndItemRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.event.resource.language.TranslationInvalidationEvent;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.mixin.lang.TranslationStorageAccessor;
import paulevs.mfb.api.SawAPI;
import paulevs.mfb.block.MFBBlocks;
import paulevs.mfb.block.SawBlock;
import paulevs.mfb.block.blockentity.DoubleSlabBlockEntity;
import paulevs.mfb.block.blockentity.FullOctaBlockEntity;
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
	private void onTileEntityRegister(BlockEntityRegisterEvent event) {
		event.register(SawBlockEntity.class, "mfb:saw");
		event.register(DoubleSlabBlockEntity.class, "mfb:double_slab");
		event.register(FullOctaBlockEntity.class, "mfb:full_octablock");
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
		
		SawAPI.addRecipe(new ItemStack(Block.PLANKS), new ItemStack(Block.STONE_SLAB, 2, 2));
		SawAPI.addRecipe(new ItemStack(Block.PLANKS), new ItemStack(Block.WOODEN_PRESSURE_PLATE));
		SawAPI.addRecipe(new ItemStack(Block.PLANKS), new ItemStack(Block.WOOD_STAIRS));
		SawAPI.addRecipe(new ItemStack(Block.PLANKS), new ItemStack(Block.FENCE));
		
		SawAPI.addRecipe(new ItemStack(Block.LOG), new ItemStack(Block.PLANKS, 6));
		
		SawAPI.addRecipe(new ItemStack(Block.COBBLESTONE), new ItemStack(Block.STONE_SLAB, 2, 3));
		SawAPI.addRecipe(new ItemStack(Block.COBBLESTONE), new ItemStack(Block.COBBLESTONE_STAIRS));
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
