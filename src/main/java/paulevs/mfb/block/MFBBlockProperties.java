package paulevs.mfb.block;

import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.state.property.IntProperty;
import net.modificationstation.stationapi.api.util.math.Direction;

public class MFBBlockProperties {
	public static final EnumProperty<Direction> FACING = EnumProperty.of("facing", Direction.class, dir -> dir.getOffsetY() == 0);
	public static final BooleanProperty EMPTY = BooleanProperty.of("empty");
	public static final IntProperty LIGHT = IntProperty.of("light", 0, 15);
	public static final IntProperty OCTABLOCK = IntProperty.of("octablock", 0, 26);
}
