package net.labymod.addons.nametags;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.impl.AddonConfig;

/**
 * The name tag configuration.
 */
@SuppressWarnings("FieldMayBeFinal")
@Singleton
@ConfigName("settings")
public final class NameTagConfiguration extends AddonConfig {

  @SwitchSetting
  private boolean enabled;

  private Map<String, CustomTag> customTags = new HashMap<>();

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  public Map<String, CustomTag> getCustomTags() {
    return this.customTags;
  }
}
