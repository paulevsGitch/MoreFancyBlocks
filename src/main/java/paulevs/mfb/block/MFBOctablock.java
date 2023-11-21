package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.maths.BlockPos;
import net.minecraft.util.maths.Box;
import net.minecraft.util.maths.MathHelper;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.mfb.block.blockentity.FullOctaBlockEntity;
import paulevs.vbe.utils.LevelUtil;

public class MFBOctablock extends TemplateBlock {
	private final Block source;
	private final byte meta;
	
	public MFBOctablock(Identifier id, Block source, byte meta) {
		super(id, source.material);
		this.source = source;
		this.meta = meta;
		setLightOpacity(0);
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setSounds(source.sounds);
		setTranslationKey(id.toString());
		NO_AMBIENT_OCCLUSION[this.id] = true;
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(MFBBlockProperties.OCTABLOCK);
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
	public int getColorMultiplier(BlockView view, int x, int y, int z) {
		return source.getColorMultiplier(view, x, y, z);
	}
	
	@Override
	public int getBaseColor(int meta) {
		return source.getBaseColor(meta);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getRenderPass() {
		return source.getRenderPass();
	}
	
	@Override
	public int getTexture(int side) {
		return source.getTexture(wrapSide(side), this.meta);
	}
	
	@Override
	public int getTexture(int side, int meta) {
		return source.getTexture(wrapSide(side), this.meta);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		if (context.getPlayer() == null) return getDefaultState();
		BlockPos pos = context.getBlockPos();
		return getState(context.getWorld(), context.getPlayer(), pos.x, pos.y, pos.z);
	}
	
	// Item Bounding Box
	@Override
	@Environment(EnvType.CLIENT)
	public void updateRenderBounds() {
		this.setBoundingBox(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
	}
	
	@Override
	public void updateBoundingBox(BlockView view, int x, int y, int z) {
		if (!(view instanceof BlockStateView bsView)) {
			this.setBoundingBox(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
			return;
		}
		updateBox(bsView.getBlockState(x, y, z));
	}
	
	@Override
	public Box getCollisionShape(Level level, int x, int y, int z) {
		updateBox(level.getBlockState(x, y, z));
		return super.getCollisionShape(level, x, y, z);
	}
	
	private void updateBox(BlockState state) {
		if (!(state.getBlock() instanceof MFBOctablock)) return;
		int octablock = state.get(MFBBlockProperties.OCTABLOCK);
		minX = (float) (octablock % 3) * 0.25F;
		minY = (float) ((octablock / 3) % 3) * 0.25F;
		minZ = (float) (octablock / 9) * 0.25F;
		maxX = minX + 0.5F;
		maxY = minY + 0.5F;
		maxZ = minZ + 0.5F;
	}
	
	private int wrapSide(int side) {
		return Math.min(side, 2);
	}
	
	@Override
	public boolean canUse(Level level, int x, int y, int z, PlayerEntity player) {
		ItemStack stack = player.inventory.getHeldItem();
		if (stack == null || !(stack.getType() instanceof BlockItem item)) return false;
		if (!(item.getBlock() instanceof MFBOctablock block)) return false;
		
		BlockState state = level.getBlockState(x, y, z);
		if (state.getBlock() instanceof MFBOctablock) {
			level.setBlockState(x, y, z, MFBBlocks.FULL_OCTABLOCK.getDefaultState());
			FullOctaBlockEntity entity = (FullOctaBlockEntity) level.getBlockEntity(x, y, z);
			entity.setOctablock(state);
			
			BlockState state2 = block.getState(level, player, x, y, z);
			if (!entity.setOctablock(state2)) {
				level.setBlockState(x, y, z, state);
				level.removeBlockEntity(x, y, z);
			}
			else {
				level.updateBlock(x, y, z);
			}
		}
		
		return true;
	}
	
	protected BlockState getState(Level level, PlayerEntity player, int x, int y, int z) {
		HitResult hit = LevelUtil.raycast(level, player);
		Direction dir = Direction.byId(hit.facing);
		float dx = (float) (hit.pos.x - x + dir.getOffsetX() * 0.25F);
		float dy = (float) (hit.pos.y - y + dir.getOffsetY() * 0.25F);
		float dz = (float) (hit.pos.z - z + dir.getOffsetZ() * 0.25F);
		return getDefaultState().with(MFBBlockProperties.OCTABLOCK, getOctaProperty(dx, dy, dz));
	}
	
	public static int getOctaProperty(float dx, float dy, float dz) {
		int octablock = clamp(MathHelper.floor(dx * 3));
		octablock += clamp(MathHelper.floor(dy * 3)) * 3;
		return octablock + clamp(MathHelper.floor(dz * 3)) * 9;
	}
	
	private static int clamp(int value) {
		return net.modificationstation.stationapi.api.util.math.MathHelper.clamp(value, 0, 2);
	}
}
