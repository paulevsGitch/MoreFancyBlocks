package paulevs.mfb.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import paulevs.mfb.block.SawBlock;
import paulevs.mfb.container.SawContainer;
import paulevs.mfb.screen.SawScreen;
import uk.co.benjiweber.expressions.tuple.BiTuple;

public class ClientListener {
	@EventListener
	public void onGUIRegister(GuiHandlerRegistryEvent event) {
		event.registry.registerValueNoMessage(
			SawBlock.GUI_ID, BiTuple.of((player, inventory) -> new SawScreen(new SawContainer(player.inventory, SawBlock.currentEntity)), null)
		);
	}
}
