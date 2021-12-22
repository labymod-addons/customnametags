package net.labymod.addons.nametags;

import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.impl.AddonConfig;

/**
 * The name tag configuration.
 */
@ConfigName("settings")
public final class NameTagConfiguration extends AddonConfig {

  @Override
  public boolean isEnabled() {
    return false;
  }
}
