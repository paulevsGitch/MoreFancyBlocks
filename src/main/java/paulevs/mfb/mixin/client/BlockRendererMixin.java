package paulevs.mfb.mixin.client;

import net.minecraft.block.Block;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import paulevs.mfb.block.MFBWallBlock;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
	@Shadow public boolean itemColorEnabled;
	@Shadow private BlockView blockView;
	
	@Shadow public abstract boolean renderFullCube(Block block, int x, int y, int z);
	@Shadow public abstract void renderBottomFace(Block arg, double d, double e, double f, int i);
	@Shadow public abstract void renderTopFace(Block arg, double d, double e, double f, int i);
	@Shadow public abstract void renderNorthFace(Block arg, double d, double e, double f, int i);
	@Shadow public abstract void renderSouthFace(Block arg, double d, double e, double f, int i);
	@Shadow public abstract void renderEastFace(Block arg, double d, double e, double f, int i);
	@Shadow public abstract void renderWestFace(Block arg, double d, double e, double f, int i);
	
	@Inject(method = "renderFence", at = @At("HEAD"), cancellable = true)
	private void mfb_renderWall(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> info) {
		if (!(block instanceof MFBWallBlock wallBlock)) return;
		if (!(blockView instanceof BlockStateView blockStateView)) return;
		
		boolean[] connections = wallBlock.getConnections(blockStateView, x, y, z);
		float maxY = wallBlock.extendWalls(blockStateView, x, y, z) ? 1.0F : 0.8125F;
		
		for (byte i = 0; i < 4; i++) {
			Direction dir = Direction.fromHorizontal(i);
			if (connections[i]) {
				float minX = 0.3125F;
				float minZ = 0.3125F;
				float maxX = 0.6875F;
				float maxZ = 0.6875F;
				
				if (dir.getOffsetX() < 0) { minX = 0; maxX = 0.5F; }
				if (dir.getOffsetX() > 0) { maxX = 1; minX = 0.5F; }
				if (dir.getOffsetZ() < 0) { minZ = 0; maxZ = 0.5F; }
				if (dir.getOffsetZ() > 0) { maxZ = 1; minZ = 0.5F; }
				
				wallBlock.setBoundingBox(minX, 0.0F, minZ, maxX, maxY, maxZ);
				renderFullCube(wallBlock, x, y, z);
			}
		}
		
		if (wallBlock.hasPost(blockStateView, x, y, z, connections)) {
			wallBlock.setBoundingBox(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
			renderFullCube(wallBlock, x, y, z);
		}
		
		info.setReturnValue(true);
	}
	
	@Inject(method = "renderBlockItem", at = @At("HEAD"), cancellable = true)
	private void mfb_renderWallItem(Block block, int meta, float light, CallbackInfo info) {
		if (!(block instanceof MFBWallBlock)) return;
		info.cancel();
		
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		if (this.itemColorEnabled) {
			int rgb = block.getBaseColor(meta);
			float r = ((rgb >> 16) & 255) / 255F;
			float g = ((rgb >> 8) & 255) / 255F;
			float b = (rgb & 255) / 255F;
			GL11.glColor3f(r * light, g * light, b * light);
		}
		
		Tessellator tessellator = Tessellator.INSTANCE;
		tessellator.start();
		
		mfb_renderBox(block, meta, 0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
		mfb_renderBox(block, meta, 0.3125F, 0.0F, 0.0F, 0.6875F, 0.8125F, 1.0F);
		
		tessellator.render();
		
		GL11.glPopMatrix();
	}
	
	@Unique
	private void mfb_renderBox(Block block, int meta, float x1, float y1, float z1, float x2, float y2, float z2) {
		Tessellator tessellator = Tessellator.INSTANCE;
		block.setBoundingBox(x1, y1, z1, x2, y2, z2);
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderBottomFace(block, 0, 0, 0, block.getTexture(0, meta));
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderTopFace(block, 0, 0, 0, block.getTexture(1, meta));
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderNorthFace(block, 0, 0, 0, block.getTexture(4, meta));
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderSouthFace(block, 0, 0, 0, block.getTexture(5, meta));
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderEastFace(block, 0, 0, 0, block.getTexture(2, meta));
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderWestFace(block, 0, 0, 0, block.getTexture(3, meta));
	}
}
