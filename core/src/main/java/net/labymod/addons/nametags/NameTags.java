package net.labymod.addons.nametags;

import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.labymod.addons.nametags.gui.activity.NameTagActivity;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.SettingOpenActivityWidget;
import net.labymod.api.configuration.loader.ConfigurationLoader;
import net.labymod.api.configuration.settings.SettingsRegistry;
import net.labymod.api.configuration.settings.gui.SettingCategoryRegistry;
import net.labymod.api.event.Priority;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameInitializeEvent;
import net.labymod.api.event.client.lifecycle.GameInitializeEvent.Lifecycle;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;
import net.labymod.api.event.labymod.config.ConfigurationSaveEvent;
import net.labymod.api.event.labymod.config.SettingWidgetInitializeEvent;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
@Singleton
public class NameTags {

  private static final Pattern COLOR_PATTERN = Pattern.compile("[0123456789abcdefr]");
  private static final Pattern FORMAT_PATTERN = Pattern.compile("[lmno]");

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
      //     this.configuration.getCustomTags().put("Metafrage", CustomTag.create(true, "lol", false));
      //    this.configuration.getCustomTags().put("JumpingPxl", CustomTag.create(true, "&4Jumping&cHatschxl", false));
      //     configurationLoader.save(configuration);

      SettingsRegistry registry = this.configuration.initializeRegistry();
      this.categoryRegistry.register("nametags.settings", registry);
    } catch (Exception e) {
      e.printStackTrace();
    }

//    NavigationRegistry navigationRegistry = this.labyAPI.getNavigationService();
//    navigationRegistry.register("nametags",
//        this.labyAPI.getInjected(NameTagNavigationElement.class));
  }

  @Subscribe
  public void onSettingWidgetInitialize(SettingWidgetInitializeEvent event) {
    if (!event.getLayer().getId().equals("nametags.settings")) {
      return;
    }

    SettingOpenActivityWidget activityWidget = new SettingOpenActivityWidget(null,
        Component.text("NameTags"),
        () -> {
          event.getParentScreen().displayScreen(this.labyAPI.getInjected(NameTagActivity.class));
        });
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
      event.getLoader().save(this.configuration);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Subscribe
  public void onPlayerNameTagRender(PlayerNameTagRenderEvent event) {
    Player player = event.getPlayer();
    TextComponent customNameTag = player.getDataWatcher()
        .computeIfAbsent("customNameTag", absent -> {
          for (Entry<String, CustomTag> customTagEntry : this.configuration.getCustomTags()
              .entrySet()) {
            CustomTag customTag = customTagEntry.getValue();
            if (customTag.isEnabled() && customTagEntry.getKey()
                .equalsIgnoreCase(player.getName())) {
              return customTag.getComponent();
            }
          }

          return Component.empty();
        });

    if (customNameTag == Component.empty()) {
      return;
    }

    TextComponent component = (TextComponent) event.getComponent();
    if (component.content().contains(player.getName())) {
      List<Component> children = new ArrayList<>();
      children.add(customNameTag);
      children.addAll(component.children());
      component = Component.empty().children(children);
    } else {
      List<TextComponent> children = new ArrayList<>();
      for (int i = 0; i < component.children().size(); i++) {
        TextComponent childComponent = (TextComponent) component.children().get(i);
        if (childComponent.content().contains(player.getName())) {
          children.add(customNameTag);
        } else {
          children.add(childComponent);
        }
      }

      component = component.children(children);
    }

    event.setComponent(component);
  }
}
