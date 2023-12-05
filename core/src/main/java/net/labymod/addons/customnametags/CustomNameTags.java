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

package net.labymod.addons.customnametags;

import java.util.function.Supplier;
import net.labymod.addons.customnametags.listener.ChatReceiveListener;
import net.labymod.addons.customnametags.listener.NameTagBackgroundRenderListener;
import net.labymod.addons.customnametags.listener.PlayerNameTagRenderListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.TranslatableComponent;
import net.labymod.api.event.client.gui.screen.playerlist.PlayerListUpdateEvent;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class CustomNameTags extends LabyAddon<CustomNameTagsConfiguration> {

  private static CustomNameTags instance;

  public CustomNameTags() {
    instance = this;
  }

  public static CustomNameTags get() {
    return instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();
    this.configuration().removeInvalidNameTags();

    this.registerListener(new ChatReceiveListener(this));
    this.registerListener(new NameTagBackgroundRenderListener(this));
    this.registerListener(new PlayerNameTagRenderListener(this));

    if (this.wasLoadedInRuntime()) {
      this.reloadTabList();
    }
  }

  @Override
  protected Class<CustomNameTagsConfiguration> configurationClass() {
    return CustomNameTagsConfiguration.class;
  }

  public void reloadTabList() {
    this.labyAPI().eventBus().fire(new PlayerListUpdateEvent());
  }

  public boolean replaceUsername(
      Component component,
      String playerName,
      Supplier<Component> customName
  ) {
    boolean replaced = false;
    for (Component child : component.getChildren()) {
      if (this.replaceUsername(child, playerName, customName)) {
        replaced = true;
      }
    }

    if (component instanceof TranslatableComponent) {
      for (Component argument : ((TranslatableComponent) component).getArguments()) {
        if (this.replaceUsername(argument, playerName, customName)) {
          replaced = true;
        }
      }
    }

    if (component instanceof TextComponent textComponent) {
      String text = textComponent.getText();

      int next = text.indexOf(playerName);
      if (next != -1) {
        replaced = true;
        int length = text.length();
        if (next == 0) {
          if (length == playerName.length()) {
            textComponent.text("");
            component.append(0, customName.get());
            return true;
          }

          if (length > playerName.length() && text.charAt(playerName.length()) != ' ') {
            return false;
          }
        }

        textComponent.text("");
        int lastNameAt = 0;
        int childIndex = 0;
        for (int i = 0; i < length; i++) {
          if (i != next) {
            continue;
          }

          if (i > lastNameAt) {
            component.append(childIndex++, Component.text(text.substring(lastNameAt, i)));
          }

          component.append(childIndex++, customName.get());
          lastNameAt = i + playerName.length();
        }

        // no way to properly check for this in chat
        if (lastNameAt < length) {
          component.append(childIndex, Component.text(text.substring(lastNameAt)));
        }
      }
    }

    return replaced;
  }
}
