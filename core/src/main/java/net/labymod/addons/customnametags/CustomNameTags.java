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

import com.google.inject.Singleton;
import java.util.function.Supplier;
import net.labymod.addons.customnametags.listener.ChatReceiveListener;
import net.labymod.addons.customnametags.listener.PlayerNameTagRenderListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.event.client.scoreboard.TabListUpdateEvent;
import net.labymod.api.models.addon.annotation.AddonListener;

@AddonListener
@Singleton
public class CustomNameTags extends LabyAddon<CustomNameTagsConfiguration> {

  @Override
  protected void enable() {
    this.registerSettingCategory();
    this.configuration().removeInvalidNameTags();

    this.registerListener(ChatReceiveListener.class);
    this.registerListener(PlayerNameTagRenderListener.class);

    if (this.wasLoadedInRuntime()) {
      this.reloadTabList();
    }
  }

  @Override
  protected Class<CustomNameTagsConfiguration> configurationClass() {
    return CustomNameTagsConfiguration.class;
  }

  public void reloadTabList() {
    this.labyAPI().eventBus().fire(new TabListUpdateEvent());
  }

  public boolean replaceUsername(Component component, String playerName,
      Supplier<Component> customName) {
    boolean replaced = false;
    for (Component child : component.getChildren()) {
      if (this.replaceUsername(child, playerName, customName)) {
        replaced = true;
      }
    }

    if (component instanceof TextComponent) {
      TextComponent textComponent = (TextComponent) component;
      String text = textComponent.getText();

      int next = text.indexOf(playerName);
      if (next != -1) {
        replaced = true;
        textComponent.text("");
        if (next == 0 && text.length() == playerName.length()) {
          component.append(0, customName.get());
        } else {
          int lastNameAt = 0;
          int childIndex = 0;
          for (int i = 0; i < text.length(); i++) {
            if (i != next) {
              continue;
            }

            if (i > lastNameAt) {
              component.append(childIndex++, Component.text(text.substring(lastNameAt, i)));
            }

            component.append(childIndex++, customName.get());
            lastNameAt = i + playerName.length();
          }

          if(lastNameAt < text.length()) {
            component.append(childIndex, Component.text(text.substring(lastNameAt)));
          }
        }
      }
    }

    return replaced;
  }
}
