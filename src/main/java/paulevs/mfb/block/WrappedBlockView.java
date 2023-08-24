package paulevs.mfb.block;

import net.minecraft.block.entity.BaseBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.level.BlockView;
import net.minecraft.level.gen.BiomeSource;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.maths.MutableBlockPos;
import net.modificationstation.stationapi.api.world.BlockStateView;

public class WrappedBlockView implements BlockView, BlockStateView {
	private final MutableBlockPos blockPos = new MutableBlockPos(0, 0, 0);
	private BlockView blockView;
	private BlockState state;
	
	public void setData(BlockView blockView, int x, int y, int z, BlockState state) {
		this.blockView = blockView;
		this.blockPos.set(x, y, z);
		this.state = state;
	}
	
	private boolean posEqual(int x, int y, int z) {
		return blockPos.x == x && blockPos.y == y && blockPos.z == z;
	}
	
	@Override
	public int getBlockId(int x, int y, int z) {
		if (posEqual(x, y, z)) return state.getBlock().id;
		return blockView.getBlockId(x, y, z);
	}
	
	@Override
	public BaseBlockEntity getBlockEntity(int x, int y, int z) {
		return blockView.getBlockEntity(x, y, z);
	}
	
	@Override
	public float getLight(int x, int y, int z, int l) {
		return blockView.getLight(x, y, z, l);
	}
	
	@Override
	public float getBrightness(int x, int y, int z) {
		return blockView.getBrightness(x, y, z);
	}
	
	@Override
	public int getBlockMeta(int x, int y, int z) {
		return blockView.getBlockMeta(x, y, z);
	}
	
	@Override
	public Material getMaterial(int x, int y, int z) {
		if (posEqual(x, y, z)) return state.getMaterial();
		return blockView.getMaterial(x, y, z);
	}
	
	@Override
	public boolean isFullOpaque(int x, int y, int z) {
		if (posEqual(x, y, z)) return state.getBlock().isFullOpaque();
		return blockView.isFullOpaque(x, y, z);
	}
	
	@Override
	public boolean canSuffocate(int x, int y, int z) {
		if (posEqual(x, y, z)) return state.getBlock().isFullCube() && state.getBlock().isFullOpaque();
		return blockView.canSuffocate(x, y, z);
	}
	
	@Override
	public BiomeSource getBiomeSource() {
		return blockView.getBiomeSource();
	}
	
	@Override
	public BlockState getBlockState(int x, int y, int z) {
		return posEqual(x, y, z) ? state : ((BlockStateView) blockView).getBlockState(x, y, z);
	}
}
