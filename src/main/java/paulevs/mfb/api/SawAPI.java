package paulevs.mfb.api;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BaseBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SawAPI {
	private static final List<Pair<ItemStack, List<ItemStack>>> RECIPES = new ArrayList<>();
	private static List<RecipePromise> futureRecipes = new ArrayList<>();
	
	/**
	 * Add recipe for future adding. Should be called instead of addRecipe when block was just added
	 */
	public static void addRecipe(BaseBlock source, int sourceMeta, BaseBlock result, int resultMeta, int count) {
		futureRecipes.add(new RecipePromise(source, sourceMeta, result, resultMeta, count));
	}
	
	public static void loadPromisedRecipes() {
		if (futureRecipes == null) return;
		futureRecipes.forEach(promise -> {
			ItemStack source = new ItemStack(promise.source.asItem(), 1, promise.sourceMeta);
			ItemStack result = new ItemStack(promise.result.asItem(), promise.count, promise.resultMeta);
			addRecipe(source, result);
		});
		futureRecipes = null;
	}
	
	public static void addRecipe(ItemStack source, ItemStack result) {
		Optional<Pair<ItemStack, List<ItemStack>>> optional = RECIPES.stream().filter(pair -> {
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
		return RECIPES.stream().filter(pair -> {
			ItemStack key = pair.getFirst();
			return key.isDamageAndIDIdentical(source);
		}).findFirst().map(Pair::getSecond).orElse(null);
	}
	
	private record RecipePromise(BaseBlock source, int sourceMeta, BaseBlock result, int resultMeta, int count) {}
}
