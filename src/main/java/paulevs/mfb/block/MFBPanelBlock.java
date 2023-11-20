package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.maths.BlockPos;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.vbe.block.VBEBlockProperties;

import java.util.ArrayList;

public class MFBPanelBlock extends TemplateBlock {
	private final Block source;
	private final byte meta;
	
	public MFBPanelBlock(Identifier id, Block source, byte meta) {
		super(id, source.material);
		this.source = source;
		this.meta = meta;
		setLightOpacity(Math.min(LIGHT_OPACITY[source.id], LIGHT_OPACITY[this.id]));
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setSounds(source.sounds);
		setTranslationKey(id.toString());
		NO_AMBIENT_OCCLUSION[this.id] = true;
	}
	
	@Override
	public void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(VBEBlockProperties.DIRECTION);
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
	@Environment(value= EnvType.CLIENT)
	public boolean isSideRendered(BlockView view, int x, int y, int z, int side) {
		boolean render = sideRenderLogic(view, x, y, z, side);
		if (!render && view instanceof BlockStateView blockStateView) {
			BlockState state = blockStateView.getBlockState(x, y, z);
			render = !state.isOf(this);
		}
		return render;
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
	
	// Item Bounding Box
	@Override
	@Environment(EnvType.CLIENT)
	public void updateRenderBounds() {
		this.setBoundingBox(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Level level = context.getWorld();
		BlockPos pos = context.getBlockPos();
		Direction face = context.getSide().getOpposite();
		BlockState state = level.getBlockState(pos.offset(face));
		if (state.getProperties().contains(VBEBlockProperties.DIRECTION)) {
			Direction facing = state.get(VBEBlockProperties.DIRECTION);
			if (facing.getAxis() != face.getAxis()) {
				PlayerEntity player = context.getPlayer();
				if (player != null && !player.isChild()) {
					return getDefaultState().with(VBEBlockProperties.DIRECTION, facing);
				}
			}
		}
		return getDefaultState().with(VBEBlockProperties.DIRECTION, face);
	}
	
	@Override
	public void updateBoundingBox(BlockView view, int x, int y, int z) {
		if (!(view instanceof BlockStateView bsView)) {
			this.setBoundingBox(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
			return;
		}
		
		BlockState state = bsView.getBlockState(x, y, z);
		Direction facing = state.get(VBEBlockProperties.DIRECTION);
		
		int dx = facing.getOffsetX();
		int dy = facing.getOffsetY();
		int dz = facing.getOffsetZ();
		
		float minX = dx == 0 ? 0 : dx > 0 ? 0.75F : 0;
		float minY = dy == 0 ? 0 : dy > 0 ? 0.75F : 0;
		float minZ = dz == 0 ? 0 : dz > 0 ? 0.75F : 0;
		float maxX = dx == 0 ? 1 : dx > 0 ? 1 : 0.25F;
		float maxY = dy == 0 ? 1 : dy > 0 ? 1 : 0.25F;
		float maxZ = dz == 0 ? 1 : dz > 0 ? 1 : 0.25F;
		
		this.setBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@Override
	public void doesBoxCollide(Level level, int x, int y, int z, Box box, ArrayList list) {
		updateBoundingBox(level, x, y, z);
		super.doesBoxCollide(level, x, y, z, box, list);
		this.setBoundingBox(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}
	
	private int wrapSide(int side) {
		return Math.min(side, 2);
	}
	
	private boolean sideRenderLogic(BlockView view, int x, int y, int z, int side) {
		if (!(view instanceof BlockStateView bsView)) {
			return super.isSideRendered(view, x, y, z, side);
		}
		
		Direction face = Direction.byId(side);
		BlockState selfState = bsView.getBlockState(x, y, z);
		
		if (selfState.getBlock() instanceof MFBPanelBlock) {
			Direction selfDir = selfState.get(VBEBlockProperties.DIRECTION);
			if (face == selfDir || face == selfDir.getOpposite()) {
				return super.isSideRendered(view, x, y, z, side);
			}
		}
		
		BlockState sideState = bsView.getBlockState(x - face.getOffsetX(), y - face.getOffsetY(), z - face.getOffsetZ());
		
		if (sideState.getBlock() instanceof MFBPanelBlock && selfState.getBlock() instanceof MFBPanelBlock) {
			Direction slab2 = selfState.get(VBEBlockProperties.DIRECTION);
			Direction slab1 = sideState.get(VBEBlockProperties.DIRECTION);
			return slab1 != slab2;
		}
		
		return super.isSideRendered(view, x, y, z, side);
	}
}
