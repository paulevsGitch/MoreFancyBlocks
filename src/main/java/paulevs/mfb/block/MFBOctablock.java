package paulevs.mfb.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BaseBlock;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.level.BlockView;
import net.minecraft.level.Level;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.maths.Box;
import net.minecraft.util.maths.MathHelper;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.state.StateManager.Builder;
import net.modificationstation.stationapi.api.template.block.TemplateBlockBase;
import net.modificationstation.stationapi.api.util.math.BlockPos;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.Vec3d;
import net.modificationstation.stationapi.api.world.BlockStateView;
import paulevs.vbe.block.VBEBlockProperties;
import paulevs.vbe.utils.LevelUtil;

import java.util.ArrayList;

public class MFBOctablock extends TemplateBlockBase {
	private final BaseBlock source;
	private final byte meta;
	
	public MFBOctablock(Identifier id, BaseBlock source, byte meta) {
		super(id, source.material);
		this.source = source;
		this.meta = meta;
		setLightOpacity(0);
		EMITTANCE[this.id] = EMITTANCE[source.id];
		setSounds(source.sounds);
		setTranslationKey(id.toString());
	}
	
	@Override
	public void appendProperties(Builder<BaseBlock, BlockState> builder) {
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
	public int getTextureForSide(int side) {
		return source.getTextureForSide(wrapSide(side), this.meta);
	}
	
	@Override
	public int getTextureForSide(int side, int meta) {
		return source.getTextureForSide(wrapSide(side), this.meta);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		if (context.getPlayer() == null) return getDefaultState();
		BlockPos pos = context.getBlockPos();
		HitResult hit = LevelUtil.raycast(context.getWorld(), context.getPlayer());
		float dx = (float) (hit.pos.x - pos.getX());
		float dy = (float) (hit.pos.y - pos.getY());
		float dz = (float) (hit.pos.z - pos.getZ());
		int octablock = MathHelper.floor(dx * 2 + 0.5F);
		octablock += MathHelper.floor(dy * 2 + 0.5F) * 3;
		octablock += MathHelper.floor(dz * 2 + 0.5F) * 9;
		octablock = net.modificationstation.stationapi.api.util.math.MathHelper.clamp(octablock, 0, 26);
		System.out.println(octablock);
		return getDefaultState().with(MFBBlockProperties.OCTABLOCK, octablock);
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
		if (!state.isOf(this)) return;
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
}
