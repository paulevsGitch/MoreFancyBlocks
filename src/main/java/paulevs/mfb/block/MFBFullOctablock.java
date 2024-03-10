package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSounds;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitType;
import net.minecraft.util.maths.Box;
import net.minecraft.util.maths.Vec3D;
import net.modificationstation.stationapi.api.block.BeforeBlockRemoved;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import paulevs.mfb.block.blockentity.FullOctaBlockEntity;
import paulevs.vbe.render.CustomBreakingRender;
import paulevs.vbe.utils.LevelUtil;

import java.util.ArrayList;

public class MFBFullOctablock extends TemplateBlockWithEntity implements CustomBreakingRender, BeforeBlockRemoved {
	private static FullOctaBlockEntity removedEntity;
	private static BlockState removedState;
	private static int selectedIndex;
	
	public MFBFullOctablock(Identifier id) {
		super(id, Material.STONE);
		setLightOpacity(0);
		setTranslationKey(id.toString());
		NO_AMBIENT_OCCLUSION[this.id] = true;
		setDefaultState(getDefaultState().with(MFBBlockProperties.LIGHT, 0));
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(MFBBlockProperties.LIGHT);
	}
	
	@Override
	protected BlockEntity createBlockEntity() {
		return new FullOctaBlockEntity();
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public boolean isFullOpaque() {
		return false;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public BlockState vbe_getBreakingState(BlockState state) {
		@SuppressWarnings("deprecation")
		Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
		HitResult hit = LevelUtil.raycast(minecraft.level, minecraft.player);
		
		if (hit == null || hit.type != HitType.BLOCK) return state;
		
		if (!(minecraft.level.getBlockEntity(hit.x, hit.y, hit.z) instanceof FullOctaBlockEntity entity)) {
			return state;
		}
		
		float dx = (float) (hit.pos.x - hit.x);
		float dy = (float) (hit.pos.y - hit.y);
		float dz = (float) (hit.pos.z - hit.z);
		int index = MFBOctablock.getOctaProperty(dx, dy, dz);
		BlockState stored = entity.states[index];
		
		return stored == null ? state : stored;
	}
	
	private Box selection = Box.create(0, 0, 0, 1, 1, 1);
	
	@Override
	@Environment(EnvType.CLIENT)
	public void vbe_setSelection(BlockState state, float dx, float dy, float dz) {
		//@SuppressWarnings("deprecation")
		//Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
		/*Direction dir = Direction.byId(minecraft.hitResult.facing);
		float x1 = MathHelper.floor(dx * 3 + dir.getOffsetX() * 0.1F) / 4F;
		float y1 = MathHelper.floor(dy * 3 + dir.getOffsetY() * 0.1F) / 4F;
		float z1 = MathHelper.floor(dz * 3 + dir.getOffsetZ() * 0.1F) / 4F;
		setBoundingBox(x1, y1, z1, x1 + 0.5F, y1 + 0.5F, z1 + 0.5F);*/
		
		/*HitResult hit = minecraft.hitResult;
		if (minecraft.level.getBlockEntity(hit.x, hit.y, hit.z) instanceof FullOctaBlockEntity entity) {
			int index = MFBOctablock.getOctaProperty(dx, dy, dz);
			BlockState stored = entity.states[index];
			if (stored != null) {
				float x1 = MathHelper.floor(dx * 3) / 4F;
				float y1 = MathHelper.floor(dy * 3) / 4F;
				float z1 = MathHelper.floor(dz * 3) / 4F;
				setBoundingBox(x1, y1, z1, x1 + 0.5F, y1 + 0.5F, z1 + 0.5F);
			}
		}*/
		
		//setBoundingBox(dx - 0.0125F, dy - 0.0125F, dz - 0.0125F, dx + 0.0125F, dy + 0.0125F, dz + 0.0125F);
		
		this.minX = selection.minX;
		this.minY = selection.minY;
		this.minZ = selection.minZ;
		this.maxX = selection.maxX;
		this.maxY = selection.maxY;
		this.maxZ = selection.maxZ;
	}
	
	@Override
	public Box getOutlineShape(Level level, int x, int y, int z) {
		return Box.createAndCache(
			selection.minX + x,
			selection.minY + y,
			selection.minZ + z,
			selection.maxX + x,
			selection.maxY + y,
			selection.maxZ + z
		);
	}
	
	@Override
	public void doesBoxCollide(Level level, int x, int y, int z, Box box, ArrayList list) {
		if (!(level.getBlockEntity(x, y, z) instanceof FullOctaBlockEntity entity)) return;
		for (byte i = 0; i < entity.states.length; i++) {
			if (entity.states[i] == null) continue;
			minX = (float) (i % 3) * 0.25F;
			minY = (float) ((i / 3) % 3) * 0.25F;
			minZ = (float) (i / 9) * 0.25F;
			maxX = minX + 0.5F;
			maxY = minY + 0.5F;
			maxZ = minZ + 0.5F;
			super.doesBoxCollide(level, x, y, z, box, list);
		}
		this.setBoundingBox(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}
	
	Box box = Box.create(0, 0, 0, 0, 0, 0);
	
	@Override
	public void updateBoundingBox(BlockView view, int x, int y, int z) {
		if (!(view.getBlockEntity(x, y, z) instanceof FullOctaBlockEntity entity)) return;
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
		
		selection.set(0, 0, 0, 0, 0, 0);
		
		@SuppressWarnings("deprecation")
		Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
		
		Vec3D pos = minecraft.viewEntity.getInterpolatedPosition(0);
		Vec3D dir = minecraft.viewEntity.getInterpolatedRotation(0);
		
		pos.x -= x;
		pos.y -= y;
		pos.z -= z;
		
		float hitDist = 1000;
		
		for (byte i = 0; i < entity.states.length; i++) {
			if (entity.states[i] == null) continue;
			box.minX = (float) (i % 3) * 0.25F;
			box.minY = (float) ((i / 3) % 3) * 0.25F;
			box.minZ = (float) (i / 9) * 0.25F;
			box.maxX = box.minX + 0.5;
			box.maxY = box.minY + 0.5;
			box.maxZ = box.minZ + 0.5;
			float d = hitBox(pos, dir, box);
			if (d < hitDist) {
				hitDist = d;
				selection.minX = box.minX;
				selection.minY = box.minY;
				selection.minZ = box.minZ;
				selection.maxX = box.maxX;
				selection.maxY = box.maxY;
				selection.maxZ = box.maxZ;
				selectedIndex = i;
			}
		}
		
		if (hitDist < 10) {
			this.minX = selection.minX;
			this.minY = selection.minY;
			this.minZ = selection.minZ;
			this.maxX = selection.maxX;
			this.maxY = selection.maxY;
			this.maxZ = selection.maxZ;
		}
	}
	
	@Override
	public boolean canUse(Level level, int x, int y, int z, PlayerEntity player) {
		ItemStack stack = player.inventory.getHeldItem();
		if (stack == null || !(stack.getType() instanceof BlockItem item)) return false;
		if (!(item.getBlock() instanceof MFBOctablock block)) return false;
		
		BlockState state = level.getBlockState(x, y, z);
		if (state.getBlock() instanceof MFBFullOctablock) {
			BlockState octablock = block.getState(level, player, x, y, z);
			FullOctaBlockEntity entity = (FullOctaBlockEntity) level.getBlockEntity(x, y, z);
			
			if (!entity.setOctablock(octablock)) return false;
			
			BlockSounds sound = octablock.getBlock().sounds;
			level.playSound(x + 0.5, y + 0.5, z + 0.5, sound.getWalkSound(), sound.getVolume(), sound.getPitch());
			
			int light = entity.getMaxLight();
			if (state.get(MFBBlockProperties.LIGHT) != light) {
				level.removeBlockEntity(x, y, z);
				level.setBlockState(x, y, z, state.with(MFBBlockProperties.LIGHT, light));
				entity.validate();
				level.setBlockEntity(x, y, z, entity);
			}
			
			level.updateBlock(x, y, z);
		}
		
		return true;
	}
	
	@Override
	public void beforeBlockRemoved(Level level, int x, int y, int z) {
		removedEntity = (FullOctaBlockEntity) level.getBlockEntity(x, y, z);
		removedState = level.getBlockState(x, y, z);
	}
	
	@Override
	public void onBlockRemoved(Level level, int x, int y, int z) {
		removedEntity.states[selectedIndex] = null;
		
		BlockState state = removedEntity.getLastState();
		if (state != null) {
			level.removeBlockEntity(x, y, z);
			level.setBlockState(x, y, z, state);
			level.updateBlock(x, y, z);
		}
		else if (removedEntity.needRemoval()) {
			level.removeBlockEntity(x, y, z);
		}
		else {
			level.setBlockState(x, y, z, removedState);
			level.setBlockEntity(x, y, z, removedEntity);
			level.updateBlock(x, y, z);
		}
	}
	
	@Override
	public void afterBreak(Level level, PlayerEntity player, int x, int y, int z, int meta) {
		System.out.println("After!");
		HitResult hit = LevelUtil.raycast(level, player);
		Direction dir = Direction.byId(hit.facing);
		
		float dx = (float) (hit.pos.x - x - dir.getOffsetX() * 0.25F);
		float dy = (float) (hit.pos.y - y - dir.getOffsetY() * 0.25F);
		float dz = (float) (hit.pos.z - z - dir.getOffsetZ() * 0.25F);
		
		int index = MFBOctablock.getOctaProperty(dx, dy, dz);
		removedEntity.states[index] = null;
		
		if (removedEntity.needRemoval()) {
			level.removeBlockEntity(x, y, z);
		}
		else {
			level.setBlockState(x, y, z, removedState);
			level.setBlockEntity(x, y, z, removedEntity);
		}
	}
	
	float hitBox(Vec3D pos, Vec3D dir, Box box) {
		float tMinX = (float) ((box.minX - pos.x) / dir.x);
		float tMinY = (float) ((box.minY - pos.y) / dir.y);
		float tMinZ = (float) ((box.minZ - pos.z) / dir.z);
		
		float tMaxX = (float) ((box.maxX - pos.x) / dir.x);
		float tMaxY = (float) ((box.maxY - pos.y) / dir.y);
		float tMaxZ = (float) ((box.maxZ - pos.z) / dir.z);
		
		float t1x = Math.min(tMinX, tMaxX);
		float t1y = Math.min(tMinY, tMaxY);
		float t1z = Math.min(tMinZ, tMaxZ);
		
		float t2x = Math.max(tMinX, tMaxX);
		float t2y = Math.max(tMinY, tMaxY);
		float t2z = Math.max(tMinZ, tMaxZ);
		
		float tNear = Math.max(t1x, Math.max(t1y, t1z));
		float tFar = Math.min(t2x, Math.min(t2y, t2z));
		
		return tNear <= tFar ? tNear : 10000;
	}
}
