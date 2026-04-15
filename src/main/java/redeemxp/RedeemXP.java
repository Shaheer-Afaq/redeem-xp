package redeemxp;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedeemXP implements ModInitializer {
	public static final String MOD_ID = "redeemxp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Manager.init();
		Events.register();
		LOGGER.info("Loaded RedeemXP");
	}
}