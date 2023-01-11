/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.labymod.addons.customnametags;

import java.util.HashMap;
import java.util.Map;
import net.labymod.addons.customnametags.gui.activity.NameTagActivity;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.AddonActivityWidget.AddonActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.inject.LabyGuice;
import net.labymod.api.util.Color;
import net.labymod.api.util.MethodOrder;

/**
 * The name tag configuration.
 */
@SuppressWarnings("FieldMayBeFinal")
@ConfigName("settings")
public final class CustomNameTagsConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true).addChangeListener(
      (property, oldValue, newValue) -> LabyGuice.getInstance(CustomNameTags.class).reloadTabList()
  );

  @SwitchSetting
  private final ConfigProperty<Boolean> hideNameTagBackground = new ConfigProperty<>(false);

  @ColorPickerSetting(chroma = true)
  private final ConfigProperty<Color> color = new ConfigProperty<>(Color.ofRGB(0, 0, 0, 192));

  @Exclude
  private Map<String, CustomNameTag> customTags = new HashMap<>();

  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public ConfigProperty<Boolean> shouldHideNameTagBackground() {
    return this.hideNameTagBackground;
  }

  public ConfigProperty<Color> color() {
    return this.color;
  }

  public Map<String, CustomNameTag> getCustomTags() {
    return this.customTags;
  }

  @MethodOrder(after = "enabled")
  @AddonActivitySetting
  public Activity openNameTags() {
    return LabyGuice.getInstance(NameTagActivity.class);
  }

  public void removeInvalidNameTags() {
    this.customTags.entrySet()
        .removeIf(entry -> entry.getKey().isEmpty() || entry.getValue().getCustomName().isEmpty());
  }
}
