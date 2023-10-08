package com.mrbysco.transprotwo.datagen.client;

import com.mrbysco.transprotwo.Transprotwo;
import com.mrbysco.transprotwo.registry.TransprotwoRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

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
	}
}
