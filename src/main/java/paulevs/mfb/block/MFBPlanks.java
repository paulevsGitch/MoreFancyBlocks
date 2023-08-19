package paulevs.mfb.block;

import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.block.TemplateBlockBase;

public class MFBPlanks extends TemplateBlockBase {
	public MFBPlanks(Identifier id) {
		super(id, Material.WOOD);
		setHardness(2.0f);
		setBlastResistance(5.0f);
		setSounds(WOOD_SOUNDS);
		setTranslationKey(id.toString());
	}
}
