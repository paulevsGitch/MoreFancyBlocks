package paulevs.mfb.item;

import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.util.Identifier;
import paulevs.mfb.MFB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class MFBItems {
	public static final List<Item> ITEMS = new ArrayList<>();
	
	public static final SawBladeItem STONE_SAW_BLADE = make("stone_saw_blade", SawBladeItem::new, 130);
	public static final SawBladeItem IRON_SAW_BLADE = make("iron_saw_blade", SawBladeItem::new, 250);
	public static final SawBladeItem DIAMOND_SAW_BLADE = make("diamond_saw_blade", SawBladeItem::new, 1500);
	
	private static <B extends Item> B make(String name, BiFunction<Identifier, Integer, B> constructor, int durability) {
		Identifier id = MFB.id(name);
		B item = constructor.apply(id, durability);
		ITEMS.add(item);
		return item;
	}
	
	public static void init() {}
}
