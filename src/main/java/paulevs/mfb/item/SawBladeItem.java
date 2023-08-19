package paulevs.mfb.item;

import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.item.TemplateItemBase;

public class SawBladeItem extends TemplateItemBase {
	public SawBladeItem(Identifier id, int durability) {
		super(id);
		setMaxStackSize(1);
		setDurability(durability);
		setTranslationKey(id.toString());
	}
}
