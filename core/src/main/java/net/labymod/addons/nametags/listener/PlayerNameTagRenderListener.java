package net.labymod.addons.nametags.listener;

import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.addons.nametags.NameTagConfiguration;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

public class PlayerNameTagRenderListener {

  private final NameTagConfiguration configuration;

  @Inject
  public PlayerNameTagRenderListener(NameTagConfiguration configuration) {
    this.configuration = configuration;
  }

  @Subscribe
  public void onPlayerNameTagRender(PlayerNameTagRenderEvent event) {
    Optional<CustomTag> optionalCustomTag = this.getCustomNameTag(event.getPlayer());
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

  private Optional<CustomTag> getCustomNameTag(Player player) {
    for (Entry<String, CustomTag> customTagEntry : this.configuration.getCustomTags().entrySet()) {
      CustomTag customTag = customTagEntry.getValue();
      if (customTagEntry.getKey().equalsIgnoreCase(player.getName())) {
        return Optional.of(customTag);
      }
    }

    return Optional.empty();
  }
}
