package net.labymod.addons.nametags.gui.activity;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

public class NameTagWidget extends SimpleWidget {

  private final String userName;
  private final CustomTag customTag;

  public NameTagWidget(String userName, CustomTag customTag) {
    this.userName = userName;
    this.customTag = customTag;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    Icon icon = Icon.head(
        UUID.fromString("570808b3-35f5-4cd8-a907-f55a5a19e992")); //Change to player head
    IconWidget iconWidget = new IconWidget(icon);
    iconWidget.addId("avatar");
    this.addChild(iconWidget);

    ComponentWidget nameWidget = ComponentWidget.component(
        Component.text(this.userName));
    nameWidget.addId("name");
    this.addChild(nameWidget);

    ComponentWidget customNameWidget = ComponentWidget.component(
        Component.text(this.customTag.getCustomName().replace("&", "§").replace("Â", "")));
    customNameWidget.addId("customName");
    this.addChild(customNameWidget);
  }
}
