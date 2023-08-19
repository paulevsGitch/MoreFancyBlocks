package paulevs.mfb.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.BlockEvent.BeforePlacedByItem;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.event.tileentity.TileEntityRegisterEvent;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import paulevs.mfb.block.MFBBlocks;
import paulevs.mfb.block.SawBlock;
import paulevs.mfb.block.SawBlockEntity;
import paulevs.mfb.item.MFBItems;

public class CommonListener {
	@EventListener
	public void onBlockRegister(BlockRegistryEvent event) {
		MFBBlocks.init();
	}
	
	@EventListener
	public void onItemRegister(ItemRegistryEvent event) {
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
}
