package paulevs.mfb.block;

import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.util.math.Direction;

public class MFBBlockProperties {
	public static final EnumProperty<Direction> FACING = EnumProperty.of("facing", Direction.class, dir -> dir.getOffsetY() == 0);
	public static final BooleanProperty EMPTY = BooleanProperty.of("empty");
}
