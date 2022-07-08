package net.labymod.addons.nametags.listener;

import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.addons.nametags.NameTags;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

public class PlayerNameTagRenderListener {

  private final NameTags addon;

  @Inject
  public PlayerNameTagRenderListener(NameTags addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onPlayerNameTagRender(PlayerNameTagRenderEvent event) {
    if (!this.addon.configuration().isEnabled()) {
      return;
    }

    String playerName = event.playerInfo().getProfile().getUsername();
    Optional<CustomTag> optionalCustomTag = this.getCustomNameTag(playerName);
    if (!optionalCustomTag.isPresent() || !optionalCustomTag.get().isEnabled()) {
      return;
    }

    CustomTag customTag = optionalCustomTag.get();
    if (customTag.isReplaceScoreboard()) {
      event.setNameTag(customTag.getComponent());
    } else {
      event.setNameTag(event.nameTag().replaceText(TextReplacementConfig.builder()
          .matchLiteral(playerName)
          .replacement(Component.empty().append(customTag.getComponent()))
          .build()));
    }
  }

  private Optional<CustomTag> getCustomNameTag(String playerName) {
    for (Entry<String, CustomTag> customTagEntry : this.addon.configuration().getCustomTags()
        .entrySet()) {
      CustomTag customTag = customTagEntry.getValue();
      if (customTagEntry.getKey().equalsIgnoreCase(playerName)) {
        return Optional.of(customTag);
      }
    }

    return Optional.empty();
  }
}
