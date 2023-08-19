package paulevs.mfb.block;

import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.block.TemplateBlockBase;

public class MFBBricks extends TemplateBlockBase {
	public MFBBricks(Identifier id) {
		super(id, Material.WOOD);
		setHardness(1.5f);
		setBlastResistance(10.0f);
		setSounds(STONE_SOUNDS);
		setTranslationKey(id.toString());
	}
}
