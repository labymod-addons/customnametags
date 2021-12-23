package net.labymod.addons.nametags;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.impl.AddonConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * The name tag configuration.
 */
@SuppressWarnings("FieldMayBeFinal")
@ConfigName("settings")
public final class NameTagConfiguration extends AddonConfig {

  @SwitchSetting
  private boolean enabled;

  private List<CustomTag> customTags = new ArrayList<>();

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  public List<CustomTag> getCustomTags() {
    return this.customTags;
  }
}
