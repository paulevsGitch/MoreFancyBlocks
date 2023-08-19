package paulevs.mfb.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import paulevs.mfb.MFB;
import paulevs.mfb.api.SawAPI;
import paulevs.vbe.block.VBEFullSlabBlock;
import paulevs.vbe.block.VBEHalfSlabBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MFBBlocks {
	public static final List<BaseBlock> BLOCKS_WITH_ITEMS = new ArrayList<>();
	public static final List<BaseBlock> SOURCE_BLOCKS = new ArrayList<>();
	public static final List<BaseBlock> SLABS = new ArrayList<>();
	
	public static final BaseBlock WOOD_SAW = make("wood_saw", SawBlock::new).setSounds(BaseBlock.WOOD_SOUNDS);
	// public static final BaseBlock STONE_SAW = make("stone_saw", SawBlock::new).setSounds(BaseBlock.STONE_SOUNDS);
	
	/*public static final BaseBlock SPRUCE_PLANKS = make("spruce_planks", MFBPlanks::new);
	public static final BaseBlock BIRCH_PLANKS = make("birch_planks", MFBPlanks::new);
	public static final BaseBlock STONE_BRICKS = make("stone_bricks", MFBBricks::new);
	public static final BaseBlock STONE_BRICKS_CRACKED = make("stone_bricks_cracked", MFBBricks::new);
	public static final BaseBlock STONE_BRICKS_MOSSY = make("stone_bricks_mossy", MFBBricks::new);*/
	
	private static <B extends BaseBlock> B make(String name, Function<Identifier, B> constructor) {
		B block = makeNI(name, constructor);
		BLOCKS_WITH_ITEMS.add(block);
		return block;
	}
	
	private static <B extends BaseBlock> B makeNI(String name, Function<Identifier, B> constructor) {
		Identifier id = MFB.id(name);
		B block = constructor.apply(id);
		block.setTranslationKey(id.toString());
		return block;
	}
	
	public static void init() {
		List<Pair<Identifier, BaseBlock>> blocks = new ArrayList<>();
		
		BlockRegistry.INSTANCE.forEach(block -> {
			if (!block.isFullCube()) return;
			if (block instanceof BlockWithEntity) return;
			if (BaseBlock.HAS_TILE_ENTITY[block.id]) return;
			if (block == BaseBlock.LEAVES) return;
			if (block == BaseBlock.LOG) return;
			if (block == BaseBlock.REDSTONE_ORE_LIT) return;
			if (block == BaseBlock.FURNACE_LIT) return;
			Identifier id = BlockRegistry.INSTANCE.getId(block);
			if (id == null) return;
			blocks.add(new Pair<>(id, block));
		});
		
		blocks.sort((p1, p2) -> {
			BaseBlock b1 = p1.getSecond();
			BaseBlock b2 = p2.getSecond();
			if (b1.material == b2.material) {
				return p1.getFirst().compareTo(p2.getFirst());
			}
			String n1 = b1.material.getClass().getName();
			String n2 = b2.material.getClass().getName();
			return n1.compareTo(n2);
		});
		
		blocks.forEach((pair) -> {
			Identifier id = pair.getFirst();
			BaseBlock block = pair.getSecond();
			SOURCE_BLOCKS.add(block);
			
			byte maxMeta = 0;
			if (block == BaseBlock.WOOL) maxMeta = 15;
			if (block == BaseBlock.LEAVES || block == BaseBlock.LOG) maxMeta = 3;
			
			if (maxMeta == 0) {
				VBEHalfSlabBlock halfSlabBlock = new MFBHalfSlabBlock(MFB.id(id.id + "_slab_half"), block, maxMeta);
				VBEFullSlabBlock fullSlabBlock = new MFBFullSlabBlock(MFB.id(id.id + "_slab_full"), block, maxMeta);
				halfSlabBlock.setFullBlock(fullSlabBlock);
				fullSlabBlock.setHalfBlock(halfSlabBlock);
				SLABS.add(halfSlabBlock);
				SawAPI.addRecipe(new ItemStack(block), new ItemStack(halfSlabBlock, 2));
				return;
			}
			
			for (byte i = 0; i < maxMeta; i++) {
				VBEHalfSlabBlock halfSlabBlock = new MFBHalfSlabBlock(MFB.id(id.id + "_slab_half_" + i), block, i);
				VBEFullSlabBlock fullSlabBlock = new MFBFullSlabBlock(MFB.id(id.id + "_slab_full_" + i), block, i);
				halfSlabBlock.setFullBlock(fullSlabBlock);
				fullSlabBlock.setHalfBlock(halfSlabBlock);
				SLABS.add(halfSlabBlock);
				SawAPI.addRecipe(new ItemStack(block), new ItemStack(halfSlabBlock, 2));
			}
		});
	}
}
