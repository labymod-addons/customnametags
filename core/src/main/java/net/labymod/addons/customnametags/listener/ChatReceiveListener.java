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

package net.labymod.addons.customnametags.listener;

import java.util.Map.Entry;
import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.labymod.addons.customnametags.CustomNameTag;
import net.labymod.addons.customnametags.CustomNameTags;
import net.labymod.api.event.Priority;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

public class ChatReceiveListener {

  private final CustomNameTags addon;

  @Inject
  public ChatReceiveListener(CustomNameTags addon) {
    this.addon = addon;
  }

  @Subscribe(Priority.LATEST)
  public void onChatReceive(ChatReceiveEvent event) {
    if (!this.addon.configuration().enabled().get()) {
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
