package net.labymod.addons.nametags;

import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
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
    List<Component> customName = (List<Component>) player.getDataWatcher().get("customNameTag");
    if (Objects.isNull(customName)) {
      customName = (List<Component>) player.getDataWatcher()
          .computeIfAbsent("customNameTag", absent -> {
            for (Entry<String, CustomTag> customTagEntry : this.configuration.getCustomTags()
                .entrySet()) {
              CustomTag customTag = customTagEntry.getValue();
              if (customTag.isEnabled() && customTagEntry.getKey()
                  .equalsIgnoreCase(player.getName())) {
                return this.fromString(customTag.getCustomName());
              }
            }

            return new ArrayList<>();
          });
    }

    if (customName.isEmpty()) {
      return;
    }

    TextComponent component = (TextComponent) event.getComponent();
    if (component.content().contains(player.getName())) {
      customName.addAll(component.children());
      component = component.content("").children(customName);
    } else {
      List<TextComponent> children = new ArrayList<>();
      for (int i = 0; i < component.children().size(); i++) {
        TextComponent childComponent = (TextComponent) component.children().get(i);
        if (childComponent.content().contains(player.getName())) {
          children.add(childComponent.content("").children(customName));
        } else {
          children.add(childComponent);
        }
      }

      component = component.children(children);
    }

    event.setComponent(component);
  }

  private List<TextComponent> fromString(String string) {
    List<TextComponent> children = new ArrayList<>();
    try {
      StringBuilder stringBuilder = new StringBuilder();
      Set<TextDecoration> tempFormats = new HashSet<>();
      NamedTextColor tempColor = null;
      for (int i = 0; i < string.length(); i++) {
        String character = String.valueOf(string.charAt(i));
        if (!character.equals("&")) {
          String prevCharacter = i == 0 ? null : String.valueOf(string.charAt(i - 1));
          if (Objects.nonNull(prevCharacter) && prevCharacter.equals("&")) {
            Matcher colorMatcher = COLOR_PATTERN.matcher(character);
            if (colorMatcher.find()) {
              if (Objects.nonNull(tempColor)) {
                this.buildSibling(children, stringBuilder, tempFormats, tempColor);

                stringBuilder = new StringBuilder();
                tempFormats.clear();
              }

              char colorCode = string.charAt(i);
              tempColor = this.getColor(colorCode == 'r' ? 'f' : colorCode);
            } else {
              Matcher formatMatcher = FORMAT_PATTERN.matcher(character);
              if (formatMatcher.find()) {
                tempFormats.add(this.getDecoration(string.charAt(i)));
              } else {
                stringBuilder.append(prevCharacter);
              }
            }
          } else {
            stringBuilder.append(character);
          }
        }

        if (i == string.length() - 1) {
          this.buildSibling(children, stringBuilder, tempFormats, tempColor);
          break;
        }
      }
    } catch (Exception e) {
      children.add(Component.text(string));
      e.printStackTrace();
    }

    return children;
  }

  private void buildSibling(List<TextComponent> children, StringBuilder stringBuilder,
      Set<TextDecoration> formatting, NamedTextColor color) {
    TextComponent sibling = Component.text(stringBuilder.toString());
    Style style = sibling.style();
    style = style.color(color);
    for (TextDecoration tempFormat : formatting) {
      style.decoration(tempFormat);
    }

    children.add(sibling.style(style));
  }

  private TextDecoration getDecoration(char key) {
    switch (key) {
      case 'k':
        return TextDecoration.OBFUSCATED;
      case 'l':
        return TextDecoration.BOLD;
      case 'm':
        return TextDecoration.STRIKETHROUGH;
      case 'n':
        return TextDecoration.UNDERLINED;
      case 'o':
        return TextDecoration.ITALIC;
      default:
        return null;
    }
  }

  private NamedTextColor getColor(char key) {
    switch (key) {
      case '0':
        return NamedTextColor.BLACK;
      case '1':
        return NamedTextColor.DARK_BLUE;
      case '2':
        return NamedTextColor.DARK_GREEN;
      case '3':
        return NamedTextColor.DARK_AQUA;
      case '4':
        return NamedTextColor.DARK_RED;
      case '5':
        return NamedTextColor.DARK_PURPLE;
      case '6':
        return NamedTextColor.GOLD;
      case '7':
        return NamedTextColor.GRAY;
      case '8':
        return NamedTextColor.DARK_GRAY;
      case '9':
        return NamedTextColor.BLUE;
      case 'a':
        return NamedTextColor.GREEN;
      case 'b':
        return NamedTextColor.AQUA;
      case 'c':
        return NamedTextColor.RED;
      case 'd':
        return NamedTextColor.LIGHT_PURPLE;
      case 'e':
        return NamedTextColor.YELLOW;
      case 'f':
      default:
        return NamedTextColor.WHITE;
    }
  }
}
