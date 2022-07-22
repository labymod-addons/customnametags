package net.labymod.addons.customnametags.listener;

import java.util.Map.Entry;
import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.labymod.addons.customnametags.CustomNameTag;
import net.labymod.addons.customnametags.CustomNameTags;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

public class ChatReceiveListener {

  private final CustomNameTags addon;

  @Inject
  public ChatReceiveListener(CustomNameTags addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onChatReceive(ChatReceiveEvent event) {
    if (!this.addon.configuration().isEnabled()) {
      return;
    }

    Component message = event.message();
    for (Entry<String, CustomNameTag> customTagEntry : this.addon.configuration().getCustomTags()
        .entrySet()) {
      if (customTagEntry.getValue().isEnabled()) {
        message = message.replaceText(
            TextReplacementConfig.builder().match("(?i)" + customTagEntry.getKey()).replacement(
                Component.empty().append(customTagEntry.getValue().getComponent())
                    .append(PlayerNameTagRenderEvent.EDITED_COMPONENT)).build());
      }
    }

    event.setMessage(message);
  }
}
