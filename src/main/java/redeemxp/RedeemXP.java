package redeemxp;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedeemXP implements ModInitializer {
	public static final String MOD_ID = "redeem-xp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Events.register();
		Config.init();
		Manager.init();
		LOGGER.info("Hello Fabric world!");
	}
}