package net.labymod.addons.nametags;

import com.google.inject.Singleton;
import net.labymod.addons.nametags.listener.ChatReceiveListener;
import net.labymod.addons.nametags.listener.PlayerNameTagRenderListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonListener;

@AddonListener
@Singleton
public class NameTags extends LabyAddon<NameTagConfiguration> {

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
