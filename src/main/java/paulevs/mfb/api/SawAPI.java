package paulevs.mfb.api;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BaseBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SawAPI {
	private static final List<Pair<ItemStack, List<ItemStack>>> RECIPES = new ArrayList<>();
	
	public static void addRecipe(ItemStack source, ItemStack result) {
		Optional<Pair<ItemStack, List<ItemStack>>> optional = RECIPES.stream().parallel().filter(pair -> {
			ItemStack key = pair.getFirst();
			return key.isDamageAndIDIdentical(source);
		}).findAny();
		
		List<ItemStack> results;
		if (optional.isPresent()) {
			results = optional.get().getSecond();
		}
		else {
			results = new ArrayList<>();
			RECIPES.add(new Pair<>(source, results));
		}
		
		results.add(result);
	}
	
	public static List<ItemStack> getResults(ItemStack source) {
		if (source == null) return null;
		return RECIPES.stream().parallel().filter(pair -> {
			ItemStack key = pair.getFirst();
			return key.isDamageAndIDIdentical(source);
		}).findFirst().map(Pair::getSecond).orElse(null);
	}
	
	static {
		addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.STONE_SLAB, 2, 2));
		addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.WOODEN_PRESSURE_PLATE));
		addRecipe(new ItemStack(BaseBlock.WOOD), new ItemStack(BaseBlock.WOOD_STAIRS));
		
		addRecipe(new ItemStack(BaseBlock.LOG), new ItemStack(BaseBlock.WOOD, 6));
	}
}
