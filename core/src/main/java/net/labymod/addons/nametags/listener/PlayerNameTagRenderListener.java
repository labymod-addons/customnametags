package net.labymod.addons.nametags.listener;

import java.util.Optional;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.addons.nametags.NameTags;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

public class PlayerNameTagRenderListener {

  private final NameTags nameTags;

  public PlayerNameTagRenderListener(NameTags nameTags) {
    this.nameTags = nameTags;
  }

  public void onPlayerNameTagRender(PlayerNameTagRenderEvent event) {
    Optional<CustomTag> optionalCustomTag = this.nameTags.getCustomNameTag(event.getPlayer());
    if (!optionalCustomTag.isPresent() || !optionalCustomTag.get().isEnabled()) {
      return;
    }

    CustomTag customTag = optionalCustomTag.get();
    if (customTag.isReplaceScoreboard()) {
      event.setNameTag(customTag.getCustomName());
    } else {
      event.setNameTag(event.getNameTag().replace(event.getPlayer().getName(),
          customTag.getCustomName()));
    }
  }
}
