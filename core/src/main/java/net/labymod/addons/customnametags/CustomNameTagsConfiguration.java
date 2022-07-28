package net.labymod.addons.customnametags;

import java.util.HashMap;
import java.util.Map;
import net.labymod.addons.customnametags.gui.activity.NameTagActivity;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.AddonActivityWidget.AddonActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.inject.LabyGuice;
import net.labymod.api.util.MethodOrder;

/**
 * The name tag configuration.
 */
@SuppressWarnings("FieldMayBeFinal")
@ConfigName("settings")
public final class CustomNameTagsConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  private Map<String, CustomNameTag> customTags = new HashMap<>();

  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public Map<String, CustomNameTag> getCustomTags() {
    return this.customTags;
  }

  @MethodOrder(after = "enabled")
  @AddonActivitySetting
  public Activity openNameTags() {
    return LabyGuice.getInstance(NameTagActivity.class);
  }
}
