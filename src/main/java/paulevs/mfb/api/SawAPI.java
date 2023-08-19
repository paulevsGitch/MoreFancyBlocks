package paulevs.mfb.api;

import com.mojang.datafixers.util.Pair;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SawAPI {
	private static final List<Pair<ItemStack, List<ItemStack>>> RECIPES = new ArrayList<>();
	
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
}
