package com.raiiiden.taczblueprints;

import com.raiiiden.taczblueprints.attachment.ModAttachmentTypes;
import com.raiiiden.taczblueprints.config.BlueprintConfig;
import com.raiiiden.taczblueprints.item.BlueprintRegistrar;
import com.raiiiden.taczblueprints.loot.ModLootModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TaCZBlueprints.MODID)
public class TaCZBlueprints {
  public static final String MODID = "taczblueprints";
  public static final Logger LOGGER = LogManager.getLogger();

  public TaCZBlueprints(IEventBus modEventBus, ModContainer container) {
    LOGGER.info("[{}] TaCZ Blueprints mod initializing...", MODID);

    // Register items and creative tabs
    BlueprintRegistrar.register(modEventBus);

    // Register loot modifiers (chest only, entity removed)
    ModLootModifiers.register(modEventBus);

    // Log registered chest loot modifier
    LOGGER.info("[{}] Registered loot modifier type: {}", MODID, ModLootModifiers.BLUEPRINT_CHEST_LOOT.getId());

    // Register configs
    container.registerConfig(ModConfig.Type.SERVER, BlueprintConfig.SERVER_SPEC);
    container.registerConfig(ModConfig.Type.CLIENT, BlueprintConfig.CLIENT_SPEC);

    // Event bus listeners
    modEventBus.addListener(this::commonSetup);
    modEventBus.addListener(this::onConfigLoad);

    // Commands
    NeoForge.EVENT_BUS.addListener(this::registerCommands);

    // Attachments
    ModAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
  }

  private void commonSetup(FMLCommonSetupEvent event) {
    LOGGER.info("[{}] Common setup complete", MODID);
    // Config may not be loaded yet, dynamic loot handler will use safe defaults
  }

  private void registerCommands(RegisterCommandsEvent event) {
    event.getDispatcher().register(com.raiiiden.taczblueprints.command.BlueprintCommands.register());
  }

  private void onConfigLoad(ModConfigEvent.Loading event) {
    if (event.getConfig().getSpec() == BlueprintConfig.SERVER_SPEC) {
      var enabledGuns = BlueprintConfig.SERVER.getEnabledGuns();
      LOGGER.info("[{}] Config loaded - Enabled guns whitelist size: {}", MODID, enabledGuns.size());
    }
  }
}
