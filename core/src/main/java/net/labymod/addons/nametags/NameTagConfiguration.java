package net.labymod.addons.nametags;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ConfigName;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * The name tag configuration.
 */
@SuppressWarnings("FieldMayBeFinal")
@Singleton
@ConfigName("settings")
public final class NameTagConfiguration extends Config {

  @SwitchSetting
  private boolean enabled;

  private Map<String, CustomTag> customTags = new HashMap<>();

  public boolean isEnabled() {
    return this.enabled;
  }

  public Map<String, CustomTag> getCustomTags() {
    return this.customTags;
  }
}
