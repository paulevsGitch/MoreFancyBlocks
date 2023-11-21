package paulevs.mfb.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SandBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.WoolBlock;
import net.minecraft.item.DyeItem;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import paulevs.mfb.MFB;
import paulevs.mfb.api.SawAPI;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.block.VBEFullSlabBlock;
import paulevs.vbe.block.VBEHalfSlabBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MFBBlocks {
	public static final List<Block> BLOCKS_WITH_ITEMS = new ArrayList<>();
	public static final List<Block> SOURCE_BLOCKS = new ArrayList<>();
	public static final List<Block> STAIRS = new ArrayList<>();
	public static final List<Block> SLABS = new ArrayList<>();
	public static final List<Block> PANELS = new ArrayList<>();
	public static final List<Block> FENCES = new ArrayList<>();
	public static final List<Block> WALLS = new ArrayList<>();
	public static final List<Block> OCTABLOCKS = new ArrayList<>();
	public static final List<Triple<String, String, String>> TRANSLATIONS = new ArrayList<>();
	
	public static final Block WOOD_SAW = make("wood_saw", SawBlock::new).setSounds(Block.WOOD_SOUNDS);
	// public static final Block STONE_SAW = make("stone_saw", SawBlock::new).setSounds(Block.STONE_SOUNDS);
	public static final Block DOUBLE_SLAB = makeNI("double_slab", MFBDoubleSlabBlock::new);
	public static final Block FULL_OCTABLOCK = makeNI("full_octablock", MFBFullOctablock::new);
	
	private static <B extends Block> B make(String name, Function<Identifier, B> constructor) {
		Identifier id = MFB.id(name);
		B block = constructor.apply(id);
		block.setTranslationKey(id.toString());
		return block;
	}
	
	private static <B extends Block> B makeNI(String name, Function<Identifier, B> constructor) {
		Identifier id = MFB.id(name);
		B block = constructor.apply(id);
		block.disableAutoItemRegistration();
		block.setTranslationKey(id.toString());
		return block;
	}
	
	public static void init() {
		List<Pair<Identifier, Block>> blocks = new ArrayList<>();
		
		BlockRegistry.INSTANCE.forEach(block -> {
			if (!block.isFullCube()) return;
			if (block instanceof BlockWithEntity) return;
			if (block instanceof SandBlock) return;
			if (block instanceof VBEFullSlabBlock) return;
			if (block instanceof StoneSlabBlock) return;
			if (Block.HAS_TILE_ENTITY[block.id]) return;
			if (block == Block.LEAVES) return;
			if (block == Block.LOG) return;
			if (block == Block.REDSTONE_ORE_LIT) return;
			if (block == Block.FURNACE_LIT) return;
			if (block == Block.GRASS) return;
			if (block == Block.BEDROCK) return;
			if (block == Block.LOCKED_CHEST) return;
			Identifier id = BlockRegistry.INSTANCE.getId(block);
			if (id == null) return;
			blocks.add(new Pair<>(id, block));
		});
		
		blocks.sort((p1, p2) -> {
			Block b1 = p1.getSecond();
			Block b2 = p2.getSecond();
			if (b1.material == b2.material) {
				return p1.getFirst().compareTo(p2.getFirst());
			}
			String n1 = b1.material.getClass().getName();
			String n2 = b2.material.getClass().getName();
			return n1.compareTo(n2);
		});
		
		blocks.forEach((pair) -> {
			Identifier id = pair.getFirst();
			Block block = pair.getSecond();
			SOURCE_BLOCKS.add(block);
			
			byte maxMeta = 0;
			if (block == Block.WOOL) maxMeta = 15;
			if (block == Block.LEAVES || block == Block.LOG) maxMeta = 3;
			boolean useMeta = maxMeta > 0;
			
			for (byte meta = 0; meta <= maxMeta; meta++) {
				String name = block.getTranslationKey();
				
				if (block instanceof WoolBlock) name += "." + DyeItem.NAMES[WoolBlock.getColor(meta)];
				name = name + ".name";
				
				if (block == Block.WOOD) STAIRS.add(Block.WOOD_STAIRS);
				else if (block == Block.COBBLESTONE) STAIRS.add(Block.COBBLESTONE_STAIRS);
				else {
					Identifier stairsID = MFB.id(addMeta(id.path + "_stairs", meta, useMeta));
					MFBStairsBlock stairs = new MFBStairsBlock(stairsID, block, meta);
					TRANSLATIONS.add(new ImmutableTriple<>("tile." + stairsID + ".name", name, " Stairs"));
					
					STAIRS.add(stairs);
					SawAPI.addRecipe(block, meta, stairs, 0, 1);
				}
				
				if (block == Block.WOOD) SLABS.add(VBEBlocks.OAK_SLAB_HALF);
				else if (block == Block.COBBLESTONE) {
					SLABS.add(VBEBlocks.COBBLESTONE_SLAB_HALF);
					SLABS.add(VBEBlocks.STONE_SLAB_HALF);
				}
				else if (block == Block.SANDSTONE) SLABS.add(VBEBlocks.SANDSTONE_SLAB_HALF);
				else {
					Identifier idHalf = MFB.id(addMeta(id.path + "_slab_half", meta, useMeta));
					Identifier idFull = MFB.id(addMeta(id.path + "_slab_full", meta, useMeta));
					
					VBEHalfSlabBlock halfSlabBlock = new MFBHalfSlabBlock(idHalf, block, meta);
					VBEFullSlabBlock fullSlabBlock = new MFBFullSlabBlock(idFull, block, meta);
					halfSlabBlock.setFullBlock(fullSlabBlock);
					fullSlabBlock.setHalfBlock(halfSlabBlock);
					
					TRANSLATIONS.add(new ImmutableTriple<>("tile." + idHalf + ".name", name, " Slab"));
					TRANSLATIONS.add(new ImmutableTriple<>("tile." + idFull + ".name", name, " Slab"));
					
					SLABS.add(halfSlabBlock);
					SawAPI.addRecipe(block, meta, halfSlabBlock, 0, 2);
				}
				
				if (block == Block.WOOD) FENCES.add(Block.FENCE);
				else {
					Identifier fenceID = MFB.id(addMeta(id.path + "_fence", meta, useMeta));
					MFBFenceBlock fence = new MFBFenceBlock(fenceID, block, meta);
					TRANSLATIONS.add(new ImmutableTriple<>("tile." + fenceID + ".name", name, " Fence"));
					
					FENCES.add(fence);
					SawAPI.addRecipe(block, meta, fence, 0, 1);
				}
				
				Identifier panelID = MFB.id(addMeta(id.path + "_panel", meta, useMeta));
				MFBPanelBlock panel = new MFBPanelBlock(panelID, block, meta);
				TRANSLATIONS.add(new ImmutableTriple<>("tile." + panelID + ".name", name, " Panel"));
				
				PANELS.add(panel);
				SawAPI.addRecipe(block, meta, panel, 0, 4);
				
				Identifier wallID = MFB.id(addMeta(id.path + "_wall", meta, useMeta));
				MFBWallBlock wall = new MFBWallBlock(wallID, block, meta);
				TRANSLATIONS.add(new ImmutableTriple<>("tile." + wallID + ".name", name, " Wall"));
				
				WALLS.add(wall);
				SawAPI.addRecipe(block, meta, wall, 0, 1);
				
				Identifier octablockID = MFB.id(addMeta(id.path + "_octablock", meta, useMeta));
				MFBOctablock octablock = new MFBOctablock(octablockID, block, meta);
				TRANSLATIONS.add(new ImmutableTriple<>("tile." + octablockID + ".name", name, " Octablock"));
				
				OCTABLOCKS.add(octablock);
				SawAPI.addRecipe(block, meta, octablock, 0, 8);
			}
		});
	}
	
	private static String addMeta(String base, byte meta, boolean useMeta) {
		return useMeta ? base + "_" + meta : base;
	}
}
