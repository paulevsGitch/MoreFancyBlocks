package paulevs.mfb;

import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MFB {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Namespace MOD_ID = Namespace.of("mfb");
	
	public static Identifier id(String name) {
		return MOD_ID.id(name);
	}
}
