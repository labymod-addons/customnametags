package net.labymod.addons.nametags;

import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.labymod.addons.nametags.gui.activity.NameTagActivity;
import net.labymod.addons.nametags.listener.ChatReceiveListener;
import net.labymod.addons.nametags.listener.PlayerNameTagRenderListener;
import net.labymod.api.LabyAPI;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.SettingOpenActivityWidget;
import net.labymod.api.configuration.loader.impl.JsonConfigLoader;
import net.labymod.api.configuration.settings.type.SettingRegistry;
import net.labymod.api.event.Priority;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.labymod.config.ConfigurationSaveEvent;
import net.labymod.api.event.labymod.config.SettingWidgetInitializeEvent;
import net.labymod.api.inject.LabyGuice;
import net.labymod.api.models.addon.annotation.AddonListener;
import javax.inject.Inject;

@AddonListener
@Singleton
public class NameTags extends LabyAddon<NameTagConfiguration> {

  @Subscribe
  public void onSettingWidgetInitialize(SettingWidgetInitializeEvent event) {
    if (!event.getHolder().getPath().equals("settings.nametags")) {
      return;
    }

    SettingOpenActivityWidget activityWidget = new SettingOpenActivityWidget(null,
        Component.text("NameTags"),
        () -> event.getParentScreen()
            .displayScreen(LabyGuice.getInstance(NameTagActivity.class)));
    event.getSettings().add(activityWidget);
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    this.registerListener(ChatReceiveListener.class);
    this.registerListener(PlayerNameTagRenderListener.class);
  }

  @Override
  protected Class<NameTagConfiguration> configurationClass() {
    return NameTagConfiguration.class;
  }
}
