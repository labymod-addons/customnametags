package net.labymod.addons.nametags;

import com.google.inject.Singleton;
import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.labymod.addons.nametags.gui.activity.NameTagActivity;
import net.labymod.addons.nametags.listener.ChatReceiveListener;
import net.labymod.addons.nametags.listener.PlayerNameTagRenderListener;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.SettingOpenActivityWidget;
import net.labymod.api.configuration.loader.impl.JsonConfigLoader;
import net.labymod.api.configuration.settings.type.SettingRegistry;
import net.labymod.api.event.Priority;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameInitializeEvent;
import net.labymod.api.event.client.lifecycle.GameInitializeEvent.Lifecycle;
import net.labymod.api.event.labymod.config.ConfigurationSaveEvent;
import net.labymod.api.event.labymod.config.SettingWidgetInitializeEvent;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
@Singleton
public class NameTags {

  private final LabyAPI labyAPI;
  private final JsonConfigLoader configLoader;

  private NameTagConfiguration configuration;

  @Inject
  private NameTags(LabyAPI labyAPI) {
    this.labyAPI = labyAPI;
    this.configLoader = JsonConfigLoader.createDefault();
    labyAPI.getEventBus()
        .registerListener(this, this.labyAPI.getInjected(PlayerNameTagRenderListener.class));
    labyAPI.getEventBus()
        .registerListener(this, this.labyAPI.getInjected(ChatReceiveListener.class));
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

    try {
      this.configLoader.load(NameTagConfiguration.class);

      SettingRegistry registry = SettingRegistry.namespace(this.labyAPI, this);
      registry.addSettings(this.configuration);

      // Register config in settings
      this.labyAPI.getCoreSettingRegistry().addSetting(registry);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onSettingWidgetInitialize(SettingWidgetInitializeEvent event) {
    if (!event.getHolder().getId().equals("nametags.settings")) {
      return;
    }

    SettingOpenActivityWidget activityWidget = new SettingOpenActivityWidget(null,
        Component.text("NameTags"),
        () -> event.getParentScreen()
            .displayScreen(this.labyAPI.getInjected(NameTagActivity.class)));
    event.getSettings().add(activityWidget);
  }

  /**
   * On configuration save.
   *
   * @param event the event
   */
  @Subscribe
  public void onConfigurationSave(ConfigurationSaveEvent event) {
    try {
      this.configLoader.save(this.configuration);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public NameTagConfiguration getConfiguration() {
    return this.configuration;
  }
}
