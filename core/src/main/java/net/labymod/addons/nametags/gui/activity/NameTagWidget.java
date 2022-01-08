package net.labymod.addons.nametags.gui.activity;

import net.kyori.adventure.text.Component;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

public class NameTagWidget extends SimpleWidget {

  private String userName;
  private CustomTag customTag;

  public NameTagWidget(String userName, CustomTag customTag) {
    this.userName = userName;
    this.customTag = customTag;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    IconWidget iconWidget = new IconWidget(this.getIconWidget(this.userName));
    iconWidget.addId("avatar");
    this.addChild(iconWidget);

    ComponentWidget nameWidget = ComponentWidget.component(Component.text(this.userName));
    nameWidget.addId("name");
    this.addChild(nameWidget);

    ComponentWidget customNameWidget = ComponentWidget.component(this.customTag.getComponent());
    customNameWidget.addId("custom-name");
    this.addChild(customNameWidget);
  }

  public Icon getIconWidget(String userName) {
    return Icon.head(userName.length() == 0 ? "MHF_Question" : userName);
  }

  public String getUserName() {
    return this.userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public CustomTag getCustomTag() {
    return this.customTag;
  }

  public void setCustomTag(CustomTag customTag) {
    this.customTag = customTag;
  }
}
