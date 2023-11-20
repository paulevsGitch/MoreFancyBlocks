package paulevs.mfb.item;

import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

public class SawBladeItem extends TemplateItem {
	public SawBladeItem(Identifier id, int durability) {
		super(id);
		setMaxStackSize(1);
		setDurability(durability);
		setTranslationKey(id.toString());
	}
}
