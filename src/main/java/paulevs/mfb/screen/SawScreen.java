package paulevs.mfb.screen;

import net.minecraft.client.gui.screen.container.ContainerScreen;
import org.lwjgl.opengl.GL11;
import paulevs.mfb.container.SawContainer;

public class SawScreen extends ContainerScreen {
	private int backgroundTexture = -1;
	
	public SawScreen(SawContainer container) {
		super(container);
	}
	
	@Override
	protected void renderContainerBackground(float delta) {
		if (backgroundTexture == -1) {
			backgroundTexture = minecraft.textureManager.getTextureId("/assets/mfb/stationapi/textures/gui/saw.png");
		}
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.minecraft.textureManager.bindTexture(backgroundTexture);
		int posX = (this.width - this.containerWidth) / 2;
		int posY = (this.height - this.containerHeight) / 2;
		this.blit(posX, posY, 0, 0, this.containerWidth, this.containerHeight);
		
		this.blit(posX + 52 - 6, posY + 15 - 7, this.containerWidth, 0, 12, 15);
		
		int slot = ((SawContainer) container).getSelectedSlot();
		if (slot == -1) return;
		posX += (slot % 6) * 18 + 62;
		posY += (slot / 6) * 18 + 8;
		fill(posX, posY, posX + 16, posY + 16, 0xFF373737);
	}
}
