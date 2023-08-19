package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.entity.BaseBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemStack;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Box;
import net.modificationstation.stationapi.api.block.BeforeBlockRemoved;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Direction.Axis;
import net.modificationstation.stationapi.impl.level.chunk.ChunkSection;
import net.modificationstation.stationapi.impl.level.chunk.FlattenedChunk;
import paulevs.mfb.MFB;
import paulevs.mfb.container.SawContainer;

public class SawBlock extends TemplateBlockWithEntity implements BeforeBlockRemoved {
	public static final Identifier GUI_ID = MFB.id("saw_gui");
	public static SawBlockEntity currentEntity;
	
	public SawBlock(Identifier id) {
		super(id, Material.STONE);
		setHardness(1F);
		setLightOpacity(0);
		setDefaultState(getDefaultState().with(MFBBlockProperties.EMPTY, false));
	}
	
	@Override
	public void appendProperties(Builder<BaseBlock, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(MFBBlockProperties.FACING, MFBBlockProperties.EMPTY);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return getDefaultState().with(MFBBlockProperties.FACING, context.getHorizontalPlayerFacing().getOpposite());
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
	public Box getOutlineShape(Level level, int x, int y, int z) {
		BlockState state = level.getBlockState(x, y, z);
		Direction facing = state.get(MFBBlockProperties.FACING);
		boolean empty = state.get(MFBBlockProperties.EMPTY);
		facing = empty ? facing.rotateClockwise(Axis.Y) : facing.rotateCounterclockwise(Axis.Y);
		minX = facing.getOffsetX() < 0 ? -1 : 0;
		maxX = facing.getOffsetX() > 0 ?  2 : 1;
		minZ = facing.getOffsetZ() < 0 ? -1 : 0;
		maxZ = facing.getOffsetZ() > 0 ?  2 : 1;
		maxY = 1;
		return super.getOutlineShape(level, x, y, z);
	}
	
	@Override
	public Box getCollisionShape(Level arg, int i, int j, int k) {
		minX = minY = minZ = 0;
		maxX = maxZ = 1;
		maxY = 0.75;
		return super.getCollisionShape(arg, i, j, k);
	}
	
	@Override
	public void onBlockPlaced(Level level, int x, int y, int z) {
		BlockState state = level.getBlockState(x, y, z);
		if (state.get(MFBBlockProperties.EMPTY)) return;
		level.setBlockEntity(x, y, z, createBlockEntity());
		Direction facing = state.get(MFBBlockProperties.FACING).rotateCounterclockwise(Axis.Y);
		x += facing.getOffsetX();
		z += facing.getOffsetZ();
		level.setBlockState(x, y, z, state.with(MFBBlockProperties.EMPTY, true));
	}
	
	@Override
	protected BaseBlockEntity createBlockEntity() {
		return new SawBlockEntity();
	}
	
	@Override
	public void beforeBlockRemoved(Level level, int x, int y, int z) {
		BlockState state = level.getBlockState(x, y, z);
		boolean empty = state.get(MFBBlockProperties.EMPTY);
		if (!empty) {
			dropContent(level, x, y, z);
		}
		Direction facing = state.get(MFBBlockProperties.FACING);
		facing = empty ? facing.rotateClockwise(Axis.Y) : facing.rotateCounterclockwise(Axis.Y);
		x += facing.getOffsetX();
		z += facing.getOffsetZ();
		if (empty) {
			dropContent(level, x, y, z);
		}
		int sectionY = level.getSectionIndex(y);
		ChunkSection section = ((FlattenedChunk) level.getChunkFromCache(x >> 4, z >> 4)).sections[sectionY];
		if (section != null && section.getBlockState(x & 15, y & 15, z & 15).isOf(this)) {
			section.setBlockState(x & 15, y & 15, z & 15, States.AIR.get());
			level.updateBlock(x, y, z);
		}
	}
	
	private void dropContent(Level level, int x, int y, int z) {
		if (level.getBlockEntity(x, y, z) instanceof SawBlockEntity entity) {
			for (ItemStack stack : entity.items) {
				if (stack == null || stack.count < 1) continue;
				level.spawnEntity(new ItemEntity(
					level, x + 0.5, y + 0.5, z + 0.5, stack
				));
			}
		}
	}
	
	@Override
	public boolean canUse(Level level, int x, int y, int z, PlayerBase player) {
		BlockState state = level.getBlockState(x, y, z);
		if (state.get(MFBBlockProperties.EMPTY)) {
			Direction facing = state.get(MFBBlockProperties.FACING).rotateClockwise(Axis.Y);
			x += facing.getOffsetX();
			z += facing.getOffsetZ();
		}
		BaseBlockEntity entity = level.getBlockEntity(x, y, z);
		if (entity instanceof SawBlockEntity sawBlockEntity) {
			currentEntity = sawBlockEntity;
			GuiHelper.openGUI(player, GUI_ID, null, new SawContainer(player.inventory, sawBlockEntity));
			return true;
		}
		return false;
	}
}
