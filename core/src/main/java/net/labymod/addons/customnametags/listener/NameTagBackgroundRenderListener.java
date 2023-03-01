package net.labymod.addons.customnametags.listener;

import net.labymod.addons.customnametags.CustomNameTags;
import net.labymod.addons.customnametags.CustomNameTagsConfiguration;
import net.labymod.api.client.entity.player.tag.event.NameTagBackgroundRenderEvent;
import net.labymod.api.event.Subscribe;

public class NameTagBackgroundRenderListener {

  private final CustomNameTags customNameTags;

  public NameTagBackgroundRenderListener(CustomNameTags customNameTags) {
    this.customNameTags = customNameTags;
  }

  @Subscribe
  public void onNameTagBackgroundRender(NameTagBackgroundRenderEvent event) {
    CustomNameTagsConfiguration configuration = this.customNameTags.configuration();
    event.setCancelled(configuration.shouldHideNameTagBackground().get());
    event.setColor(configuration.color().get().get());
  }
}
