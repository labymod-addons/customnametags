package net.labymod.addons.customnametags.listener;

import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.labymod.addons.customnametags.CustomNameTag;
import net.labymod.addons.customnametags.CustomNameTags;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

public class PlayerNameTagRenderListener {

  private final CustomNameTags addon;

  @Inject
  public PlayerNameTagRenderListener(CustomNameTags addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onPlayerNameTagRender(PlayerNameTagRenderEvent event) {
    if (!this.addon.configuration().isEnabled()) {
      return;
    }

    String playerName = event.playerInfo().profile().getUsername();
    Optional<CustomNameTag> optionalCustomTag = this.getCustomNameTag(playerName);
    if (!optionalCustomTag.isPresent() || !optionalCustomTag.get().isEnabled()) {
      return;
    }

    CustomNameTag customNameTag = optionalCustomTag.get();
    if (customNameTag.isReplaceScoreboard()) {
      event.setNameTag(customNameTag.getComponent());
    } else {
      event.setNameTag(event.nameTag().replaceText(TextReplacementConfig.builder()
          .matchLiteral(playerName)
          .replacement(Component.empty().append(customNameTag.getComponent()))
          .build()));
    }
  }

  private Optional<CustomNameTag> getCustomNameTag(String playerName) {
    for (Entry<String, CustomNameTag> customTagEntry : this.addon.configuration().getCustomTags()
        .entrySet()) {
      CustomNameTag customNameTag = customTagEntry.getValue();
      if (customTagEntry.getKey().equalsIgnoreCase(playerName)) {
        return Optional.of(customNameTag);
      }
    }

    return Optional.empty();
  }
}
