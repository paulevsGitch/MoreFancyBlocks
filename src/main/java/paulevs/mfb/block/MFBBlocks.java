package paulevs.mfb.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BaseBlock;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SandBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.WoolBlock;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.DyeItem;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.mixin.lang.TranslationStorageAccessor;
import paulevs.mfb.MFB;
import paulevs.mfb.api.SawAPI;
import paulevs.vbe.block.VBEBlocks;
import paulevs.vbe.block.VBEFullSlabBlock;
import paulevs.vbe.block.VBEHalfSlabBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class MFBBlocks {
	public static final List<BaseBlock> BLOCKS_WITH_ITEMS = new ArrayList<>();
	public static final List<BaseBlock> SOURCE_BLOCKS = new ArrayList<>();
	public static final List<BaseBlock> STAIRS = new ArrayList<>();
	public static final List<BaseBlock> SLABS = new ArrayList<>();
	public static final List<BaseBlock> PANELS = new ArrayList<>();
	public static final List<BaseBlock> FENCES = new ArrayList<>();
	public static final List<BaseBlock> WALLS = new ArrayList<>();
	
	public static final BaseBlock WOOD_SAW = make("wood_saw", SawBlock::new).setSounds(BaseBlock.WOOD_SOUNDS);
	// public static final BaseBlock STONE_SAW = make("stone_saw", SawBlock::new).setSounds(BaseBlock.STONE_SOUNDS);
	
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
			if (block instanceof SandBlock) return;
			if (block instanceof VBEFullSlabBlock) return;
			if (block instanceof StoneSlabBlock) return;
			if (BaseBlock.HAS_TILE_ENTITY[block.id]) return;
			if (block == BaseBlock.LEAVES) return;
			if (block == BaseBlock.LOG) return;
			if (block == BaseBlock.REDSTONE_ORE_LIT) return;
			if (block == BaseBlock.FURNACE_LIT) return;
			if (block == BaseBlock.GRASS) return;
			if (block == BaseBlock.BEDROCK) return;
			if (block == BaseBlock.LOCKED_CHEST) return;
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
		
		Properties translations = ((TranslationStorageAccessor) TranslationStorage.getInstance()).getTranslations();
		
		blocks.forEach((pair) -> {
			Identifier id = pair.getFirst();
			BaseBlock block = pair.getSecond();
			SOURCE_BLOCKS.add(block);
			
			byte maxMeta = 0;
			if (block == BaseBlock.WOOL) maxMeta = 15;
			if (block == BaseBlock.LEAVES || block == BaseBlock.LOG) maxMeta = 3;
			boolean useMeta = maxMeta > 0;
			
			for (byte meta = 0; meta <= maxMeta; meta++) {
				String name = block.getTranslationKey();
				
				if (block == BaseBlock.WOOD) STAIRS.add(BaseBlock.WOOD_STAIRS);
				else if (block == BaseBlock.COBBLESTONE) STAIRS.add(BaseBlock.COBBLESTONE_STAIRS);
				else {
					Identifier stairsID = MFB.id(addMeta(id.id + "_stairs", meta, useMeta));
					MFBStairsBlock stairs = new MFBStairsBlock(stairsID, block, meta);
					translations.put("tile." + stairsID + ".name", name + " Stairs");
					
					STAIRS.add(stairs);
					SawAPI.addRecipe(block, meta, stairs, 0, 1);
				}
				
				if (block == BaseBlock.WOOD) SLABS.add(VBEBlocks.OAK_SLAB_HALF);
				else if (block == BaseBlock.COBBLESTONE) {
					SLABS.add(VBEBlocks.COBBLESTONE_SLAB_HALF);
					SLABS.add(VBEBlocks.STONE_SLAB_HALF);
				}
				else if (block == BaseBlock.SANDSTONE) SLABS.add(VBEBlocks.SANDSTONE_SLAB_HALF);
				else {
					Identifier idHalf = MFB.id(addMeta(id.id + "_slab_half", meta, useMeta));
					Identifier idFull = MFB.id(addMeta(id.id + "_slab_full", meta, useMeta));
					
					VBEHalfSlabBlock halfSlabBlock = new MFBHalfSlabBlock(idHalf, block, meta);
					VBEFullSlabBlock fullSlabBlock = new MFBFullSlabBlock(idFull, block, meta);
					halfSlabBlock.setFullBlock(fullSlabBlock);
					fullSlabBlock.setHalfBlock(halfSlabBlock);
					
					if (block instanceof WoolBlock) name += "." + DyeItem.NAMES[WoolBlock.getColor(meta)];
					name = translations.getProperty(name + ".name", id.toString());
					
					translations.put("tile." + idHalf + ".name", name + " Slab");
					translations.put("tile." + idFull + ".name", name + " Slab");
					
					SLABS.add(halfSlabBlock);
					SawAPI.addRecipe(block, meta, halfSlabBlock, 0, 2);
				}
				
				if (block == BaseBlock.WOOD) FENCES.add(BaseBlock.FENCE);
				else {
					Identifier fenceID = MFB.id(addMeta(id.id + "_fence", meta, useMeta));
					MFBFenceBlock fence = new MFBFenceBlock(fenceID, block, meta);
					translations.put("tile." + fenceID + ".name", name + " Fence");
					
					FENCES.add(fence);
					SawAPI.addRecipe(block, meta, fence, 0, 1);
				}
				
				Identifier panelID = MFB.id(addMeta(id.id + "_panel", meta, useMeta));
				MFBPanelBlock panel = new MFBPanelBlock(panelID, block, meta);
				translations.put("tile." + panelID + ".name", name + " Panel");
				
				PANELS.add(panel);
				SawAPI.addRecipe(block, meta, panel, 0, 4);
				
				Identifier wallID = MFB.id(addMeta(id.id + "_wall", meta, useMeta));
				MFBWallBlock wall = new MFBWallBlock(wallID, block, meta);
				translations.put("tile." + wallID + ".name", name + " Wall");
				
				WALLS.add(wall);
				SawAPI.addRecipe(block, meta, wall, 0, 1);
			}
		});
	}
	
	private static String addMeta(String base, byte meta, boolean useMeta) {
		return useMeta ? base + "_" + meta : base;
	}
}
