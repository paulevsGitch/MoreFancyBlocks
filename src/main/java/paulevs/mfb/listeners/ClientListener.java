package paulevs.mfb.listeners;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.client.texture.atlas.ExpandableAtlas;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import paulevs.mfb.MFB;
import paulevs.mfb.block.MFBBlocks;
import paulevs.mfb.block.MFBFenceBlock;
import paulevs.mfb.block.SawBlock;
import paulevs.mfb.container.SawContainer;
import paulevs.mfb.client.screen.SawScreen;
import uk.co.benjiweber.expressions.tuple.BiTuple;

public class ClientListener {
	@EventListener
	public void onGUIRegister(GuiHandlerRegistryEvent event) {
		event.registry.registerValueNoMessage(
			SawBlock.GUI_ID, BiTuple.of((player, inventory) -> new SawScreen(new SawContainer(player.inventory, SawBlock.currentEntity)), null)
		);
	}
	
	@EventListener
	public void onTexturesRegister(TextureRegisterEvent event) {
		final ExpandableAtlas blockAtlas = Atlases.getTerrain();
		MFBBlocks.SOURCE_BLOCKS.forEach(block -> {
			if (block.texture != 0) return;
			BlockState state = block.getDefaultState();
			BakedModel model = StationRenderAPI.getBakedModelManager().getBlockModels().getModel(state);
			Identifier textureID = model.getSprite().getContents().getId();
			block.texture = blockAtlas.addTexture(textureID).index;
		});
		
		MFBFenceBlock fence = (MFBFenceBlock) BlockRegistry.INSTANCE.get(MFB.id("glass_fence"));
		if (fence != null) {
			fence.sideTexture = blockAtlas.addTexture(MFB.id("block/glass_fence_side")).index;
			fence.topTexture = blockAtlas.addTexture(MFB.id("block/glass_fence_top")).index;
		}
	}
}
