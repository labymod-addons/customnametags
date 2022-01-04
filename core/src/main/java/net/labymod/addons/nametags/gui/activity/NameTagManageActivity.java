package net.labymod.addons.nametags.gui.activity;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.activities.OverlayWidgetActivity;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

public class NameTagManageActivity extends OverlayWidgetActivity {

  private final String userName;
  private final CustomTag customTag;
  private final Action action;

  private TextFieldWidget nameTextField;
  private TextFieldWidget customTextField;
  private String lastUserName;
  private String lastCustomName;

  protected NameTagManageActivity(NameTagWidget nameTagWidget, String userName, CustomTag customTag,
      Action action) {
    super(nameTagWidget);

    this.userName = userName;
    this.customTag = customTag;
    this.action = action;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    DivWidget container = new DivWidget();
    container.addId("manage-container");

    ComponentWidget customNameWidget = ComponentWidget.component(this.customTag.getComponent());
    customNameWidget.addId("custom-preview");
    container.addChild(customNameWidget);

    Icon icon = Icon.head(this.userName.length() == 0 ? "MHF_Question" : this.userName);
    IconWidget iconWidget = new IconWidget(icon);
    iconWidget.addId("avatar");

    FlexibleContentWidget list = new FlexibleContentWidget(true);
    list.addId("input-list");

    ComponentWidget labelName = ComponentWidget.i18n("nametags.gui.manage.name");
    labelName.addId("label-name");
    list.addContent(labelName);

    this.nameTextField = new TextFieldWidget();
    this.nameTextField.setText(this.userName);
    this.nameTextField.setUpdateListener(newValue -> {
      if (newValue.equals(this.lastUserName)) {
        return;
      }

      this.lastUserName = newValue;
      if (newValue.length() != 0) {
        iconWidget.setIcon(Icon.head(newValue));
      }
    });

    list.addContent(this.nameTextField);
    iconWidget.getBounds().setTop(container.getBounds().getTop() + this.nameTextField.getBounds()
        .getTop());
    iconWidget.getBounds()
        .setLeft(container.getBounds().getLeft());
    container.addChild(iconWidget);

    ComponentWidget labelCustomName = ComponentWidget.i18n("nametags.gui.manage.custom.name");
    labelCustomName.addId("label-name");
    list.addContent(labelCustomName);

    this.customTextField = new TextFieldWidget();
    this.customTextField.setText(this.customTag.getCustomName());
    this.customTextField.setUpdateListener(newValue -> {
      if (newValue.equals(this.lastCustomName)) {
        return;
      }

      this.lastCustomName = newValue;
      customNameWidget.setComponent(
          LegacyComponentSerializer.legacyAmpersand().deserialize(newValue));
    });

    list.addContent(this.customTextField);
    container.addChild(list);

    this.getDocument().addChild(container);
  }

  protected enum Action {
    ADD, EDIT, REMOVE
  }
}
