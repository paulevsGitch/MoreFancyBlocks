package paulevs.mfb.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.container.ContainerScreen;
import org.lwjgl.opengl.GL11;
import paulevs.mfb.container.SawContainer;

@Environment(EnvType.CLIENT)
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
		minecraft.textureManager.bindTexture(backgroundTexture);
		int posX = (width - containerWidth) / 2;
		int posY = (height - containerHeight) / 2;
		blit(posX, posY, 0, 0, containerWidth, containerHeight);
		
		blit(posX + 52 - 6, posY + 15 - 7, containerWidth, 0, 12, 15);
		
		SawContainer sawContainer = (SawContainer) container;
		int slot = sawContainer.getSelectedSlot();
		if (slot == -1) return;
		posX += (slot % 6) * 18 + 62;
		posY += (slot / 6) * 18 + 8;
		fill(posX, posY, posX + 16, posY + 16, 0xFF373737);
		
		if (sawContainer.sound == 0) return;
		System.out.println(sawContainer.sound);
		switch (sawContainer.sound) {
			case 1 -> minecraft.soundHelper.playSound("mfb:saw_normal", 1, 1);
			case 2 -> minecraft.soundHelper.playSound("mfb:saw_break", 1, 1);
			case 3 -> minecraft.soundHelper.playSound("random.click", 1, 1);
		}
		sawContainer.sound = 0;
	}
}
