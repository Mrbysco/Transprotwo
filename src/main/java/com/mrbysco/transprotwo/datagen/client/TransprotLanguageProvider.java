package com.mrbysco.transprotwo.datagen.client;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.Nullable;

public class TransprotLanguageProvider extends LanguageProvider {

	public TransprotLanguageProvider(PackOutput packOutput) {
		super(packOutput, Transprotwo.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.transprotwo", "Transprotwo");

		addItem(TransprotwoRegistry.LINKER, "Linker");
		addItem(TransprotwoRegistry.UPGRADE_MK_I, "Upgrade MK I");
		addItem(TransprotwoRegistry.UPGRADE_MK_II, "Upgrade MK II");
		addItem(TransprotwoRegistry.UPGRADE_MK_III, "Upgrade MK III");
		addItem(TransprotwoRegistry.UPGRADE_MK_IV, "Upgrade MK IV");

		addBlock(TransprotwoRegistry.DISPATCHER, "Dispatcher");
		addBlock(TransprotwoRegistry.FLUID_DISPATCHER, "Fluid Dispatcher");
		addBlock(TransprotwoRegistry.POWER_DISPATCHER, "Power Dispatcher");

		add("transprotwo.container.dispatcher", "Dispatcher");
		add("transprotwo.container.fluid_dispatcher", "Fluid Dispatcher");
		add("transprotwo.container.power_dispatcher", "Power Dispatcher");

		add("transprotwo.networking.change_color.failed", "Failed to change color: %s");
		add("transprotwo.networking.transfer_particle.failed", "Failed to summon transfer particle: %s");
		add("transprotwo.networking.update_dispatcher.failed", "Failed to update dispatcher: %s");
		add("transprotwo.networking.update_fluid_dispatcher.failed", "Failed to update fluid dispatcher: %s");
		add("transprotwo.networking.update_power_dispatcher.failed", "Failed to update power dispatcher: %s");

		addConfig("client", "Client", "Client settings");
		addConfig("showParticles", "Show Particles", "Dictates if the particles are visible");
		addConfig("showItems", "Show Items", "Dictates if the Dispatcher renders the items traveling");
		addConfig("showFluids", "Show Fluids", "Dictates if the Dispatcher renders the fluid traveling");
		addConfig("showPower", "Show Power", "Dictates if the Dispatcher renders the power traveling");
		addConfig("general", "General", "General settings");
		addConfig("range", "Max Range", "Max distance between dispatcher and inventory [Default: 24]");

	}

	/**
	 * Add the translation for a config entry
	 *
	 * @param path        The path of the config entry
	 * @param name        The name of the config entry
	 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
	 */
	private void addConfig(String path, String name, @Nullable String description) {
		this.add("transprotwo.configuration." + path, name);
		if (description != null && !description.isEmpty())
			this.add("transprotwo.configuration." + path + ".tooltip", description);
	}
}
