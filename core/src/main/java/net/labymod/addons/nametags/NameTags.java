package net.labymod.addons.nametags;

import com.google.inject.Singleton;
import javax.inject.Inject;
import net.labymod.addons.nametags.gui.NameTagNavigationElement;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.navigation.NavigationRegistry;
import net.labymod.api.configuration.loader.ConfigurationLoader;
import net.labymod.api.configuration.settings.SettingsRegistry;
import net.labymod.api.configuration.settings.gui.SettingCategoryRegistry;
import net.labymod.api.event.Priority;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameInitializeEvent;
import net.labymod.api.event.client.lifecycle.GameInitializeEvent.Lifecycle;
import net.labymod.api.event.labymod.config.ConfigurationSaveEvent;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
@Singleton
public class NameTags {

  private final LabyAPI labyAPI;
  private final SettingCategoryRegistry categoryRegistry;

  private NameTagConfiguration configuration;

  @Inject
  private NameTags(LabyAPI labyAPI, SettingCategoryRegistry categoryRegistry) {
    this.labyAPI = labyAPI;
    this.categoryRegistry = categoryRegistry;
  }

  /**
   * On game initialize.
   *
   * @param event the event
   */
  @Subscribe(Priority.LATEST)
  public void onGameInitialize(GameInitializeEvent event) {
    if (event.getLifecycle() != Lifecycle.POST_STARTUP) {
      return;
    }

    ConfigurationLoader configurationLoader = this.labyAPI.getConfigurationLoader();
    try {
      this.configuration = configurationLoader.load(NameTagConfiguration.class);
      SettingsRegistry registry = this.configuration.initializeRegistry();

      this.categoryRegistry.register(registry.getId(), registry);
    } catch (Exception e) {
      e.printStackTrace();
    }

    NavigationRegistry navigationRegistry = this.labyAPI.getNavigationService();
    navigationRegistry.register("nametags",
        this.labyAPI.getInjected(NameTagNavigationElement.class));
  }

  /**
   * On configuration save.
   *
   * @param event the event
   */
  @Subscribe
  public void onConfigurationSave(ConfigurationSaveEvent event) {
    try {
      event.getLoader().save(this.configuration);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
