package paulevs.mfb.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.block.BaseBlock;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.block.BlockEvent.BeforePlacedByItem;
import net.modificationstation.stationapi.api.event.registry.AfterBlockAndItemRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.event.tileentity.TileEntityRegisterEvent;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import paulevs.mfb.api.SawAPI;
import paulevs.mfb.block.MFBBlocks;
import paulevs.mfb.block.SawBlock;
import paulevs.mfb.block.SawBlockEntity;
import paulevs.mfb.item.MFBItems;

public class CommonListener {
	@EventListener(priority = ListenerPriority.LOWEST)
	public void registerBLocks(BlockRegistryEvent event) {
		System.out.println("Adding blocks!");
		MFBBlocks.init();
	}
	
	@EventListener
	public void registerItems(ItemRegistryEvent event) {
		System.out.println("Adding items!");
		MFBItems.init();
	}
	
	@EventListener
	public void onTileEntityRegister(TileEntityRegisterEvent event) {
		event.register(SawBlockEntity.class, "mfb:saw");
	}
	
	@EventListener
	public void beforeItemPlace(BeforePlacedByItem event) {
		if (!(event.block instanceof SawBlock sawBlock)) return;
		Direction facing = Direction.fromRotation(event.player.yaw).rotateClockwise(Axis.Y);
		int x = event.x + facing.getOffsetX();
		int z = event.z + facing.getOffsetZ();
		if (!sawBlock.canPlaceAt(event.world, x, event.y, z)) {
			event.placeFunction = () -> false;
		}
	}
	
	@EventListener(priority = ListenerPriority.LOWEST)
	public void onRecipesRegister(AfterBlockAndItemRegisterEvent event) {
		SawAPI.loadPromisedRecipes();
		
		SawAPI.addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.STONE_SLAB, 2, 2));
		SawAPI.addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.WOODEN_PRESSURE_PLATE));
		SawAPI.addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.WOOD_STAIRS));
		SawAPI.addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.FENCE));
		
		SawAPI.addRecipe(new ItemStack(BaseBlock.LOG), new ItemStack(BaseBlock.WOOD, 6));
		
		SawAPI.addRecipe(new ItemStack(BaseBlock.COBBLESTONE), new ItemStack(BaseBlock.STONE_SLAB, 2, 3));
		SawAPI.addRecipe(new ItemStack(BaseBlock.COBBLESTONE), new ItemStack(BaseBlock.COBBLESTONE_STAIRS));
	}
}
