package paulevs.mfb.block;

import net.minecraft.block.BaseBlock;
import net.modificationstation.stationapi.api.registry.Identifier;
import paulevs.mfb.MFB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MFBBlocks {
	public static final List<BaseBlock> BLOCKS_WITH_ITEMS = new ArrayList<>();
	
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
	
	public static void init() {}
}
