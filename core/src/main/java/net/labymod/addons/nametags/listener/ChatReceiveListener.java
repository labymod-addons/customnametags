package net.labymod.addons.nametags.listener;

import java.util.Map.Entry;
import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.addons.nametags.NameTagConfiguration;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

public class ChatReceiveListener {

  private final NameTagConfiguration configuration;

  @Inject
  public ChatReceiveListener(NameTagConfiguration configuration) {
    this.configuration = configuration;
  }

  @Subscribe
  public void onChatReceive(ChatReceiveEvent event) {
    if (!this.configuration.isEnabled()) {
      return;
    }

    Component message = event.getMessage();
    for (Entry<String, CustomTag> customTagEntry : this.configuration.getCustomTags().entrySet()) {
      message = message.replaceText(TextReplacementConfig.builder()
          .match("(?i)" + customTagEntry.getKey())
          .replacement(Component.empty()
              .append(customTagEntry.getValue().getComponent())
              .append(PlayerNameTagRenderEvent.EDITED_COMPONENT))
          .build());
    }

    event.setMessage(message);
  }
}
