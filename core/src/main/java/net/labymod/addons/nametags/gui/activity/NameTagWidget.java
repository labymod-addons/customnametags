package net.labymod.addons.nametags.gui.activity;

import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

public class NameTagWidget extends SimpleWidget {

  private final TempNameTag tempNameTag;

  private TextFieldWidget nameTextField;
  private TextFieldWidget customTextField;

  public NameTagWidget(TempNameTag tempNameTag) {
    this.tempNameTag = tempNameTag;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    if (this.tempNameTag.getUserName().length() == 0) {
      this.tempNameTag.setUserName(this.tempNameTag.getDefaultUserName());
    }

    Icon icon = this.tempNameTag.isPlaceholder() ? Icon.sprite16(
        Laby.getLabyAPI().getLabyConstants().getResourceSpriteCommon(), 1, 3)
        : Icon.head(this.tempNameTag.getUserName());
    IconWidget iconWidget = new IconWidget(icon);
    iconWidget.addId("avatar");
    this.addChild(iconWidget);

    ComponentWidget nameWidget;
    if (this.tempNameTag.isPlaceholder()) {
      nameWidget = ComponentWidget.i18n("nametags.gui.manage.add");
    } else {
      nameWidget = ComponentWidget.component(
          Component.text(this.tempNameTag.getUserName()));
    }

    nameWidget.addId("name");
    this.addChild(nameWidget);

    ComponentWidget customNameWidget = ComponentWidget.component(
        this.tempNameTag.getCustomTag().getComponent());
    customNameWidget.addId("custom-name");
    this.addChild(customNameWidget);

    if (!this.hasId("name-tag-selected")) {
      return;
    }

    FlexibleContentWidget list = new FlexibleContentWidget(true);
    list.addId("input-list");

    ComponentWidget labelName = ComponentWidget.i18n("nametags.gui.manage.name");
    labelName.addId("label-name");
    list.addContent(labelName);

    this.nameTextField = new TextFieldWidget();
    this.nameTextField.addId("name-text-field");
    this.nameTextField.setText(this.tempNameTag.getUserName());
    this.nameTextField.setUpdateListener(newValue -> {
      if (newValue.equals(this.tempNameTag.getDefaultUserName())) {
        return;
      }

      this.tempNameTag.setUserName(newValue);
      nameWidget.setComponent(Component.text(newValue));
      if (newValue.length() != 0) {
        iconWidget.setIcon(Icon.head(newValue));
      }
    });

    list.addContent(this.nameTextField);

    ComponentWidget labelCustomName = ComponentWidget.i18n("nametags.gui.manage.custom.name");
    labelCustomName.addId("label-name");
    list.addContent(labelCustomName);

    this.customTextField = new TextFieldWidget();
    this.customTextField.addId("name-text-field");
    this.customTextField.setText(this.tempNameTag.getCustomTag().getCustomName());
    this.customTextField.setUpdateListener(newValue -> {
      if (newValue.equals(this.tempNameTag.getDefaultCustomName())) {
        return;
      }

      this.tempNameTag.getCustomTag().setCustomName(newValue);
      customNameWidget.setComponent(this.tempNameTag.getCustomTag().getComponent());
    });

    list.addContent(this.customTextField);
    this.addChild(list);
  }

  public TempNameTag getTempNameTag() {
    return this.tempNameTag;
  }

  public boolean isMouseOverWidget() {
    return (Objects.nonNull(this.nameTextField) && this.nameTextField.isHovered()) || (
        Objects.nonNull(
            this.customTextField) && this.customTextField.isHovered());
  }

  public void save(Map<String, CustomTag> customTags) {
    if (!this.tempNameTag.isChanged()) {
      return;
    }

    customTags.remove(this.tempNameTag.getDefaultUserName());
    customTags.put(this.tempNameTag.getUserName(), this.tempNameTag.getCustomTag());
  }
}
