package net.labymod.addons.nametags.gui.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.addons.nametags.NameTagConfiguration;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.key.InputType;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

public class NameTagActivity extends SimpleActivity {

  private final NameTagConfiguration nameTagConfiguration;
  private final VerticalListWidget<NameTagWidget> nameTagList;
  private final Map<String, NameTagWidget> nameTagWidgets;

  private ButtonWidget removeButton;
  private ButtonWidget editButton;

  private FlexibleContentWidget inputWidget;
  private String lastUserName;
  private String lastCustomName;

  private Action action;

  @Inject
  public NameTagActivity(NameTagConfiguration nameTagConfiguration) {
    this.nameTagConfiguration = nameTagConfiguration;

    this.nameTagWidgets = new HashMap<>();
    nameTagConfiguration.getCustomTags().forEach((userName, customTag) -> {
      this.nameTagWidgets.put(userName, new NameTagWidget(userName, customTag));
    });

    this.nameTagList = new VerticalListWidget<>();
    this.nameTagList.addId("name-tag-list");
    this.nameTagList.setSelectCallback(nameTagWidget -> {
      NameTagWidget selectedNameTag = this.nameTagList.getSession().getSelectedEntry();
      if (Objects.isNull(selectedNameTag)
          || selectedNameTag.getCustomTag() != nameTagWidget.getCustomTag()) {
        this.editButton.setEnabled(true);
        this.removeButton.setEnabled(true);
      }
    });
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.renderBackground = false;

    DivWidget listContainer = new DivWidget();
    listContainer.addId("name-tag-container");
    for (NameTagWidget nameTagWidget : this.nameTagWidgets.values()) {
      this.nameTagList.addChild(nameTagWidget);
    }

    listContainer.addChild(new ScrollWidget(this.nameTagList));
    this.getDocument().addChild(listContainer);

    NameTagWidget selectedNameTag = this.nameTagList.getSession().getSelectedEntry();
    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("overview-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.add", () -> this.setAction(Action.ADD)));
    menu.addEntry(this.editButton = ButtonWidget.i18n("labymod.ui.button.edit",
        () -> this.setAction(Action.EDIT)).setEnabled(Objects.nonNull(selectedNameTag)));
    menu.addEntry(this.removeButton = ButtonWidget.i18n("labymod.ui.button.remove",
        () -> this.setAction(Action.REMOVE)).setEnabled(Objects.nonNull(selectedNameTag)));

    this.getDocument().addChild(menu);
    if (Objects.isNull(this.action)) {
      return;
    }
    DivWidget manageContainer = new DivWidget();
    manageContainer.addId("manage-container");

    Widget overlayWidget;
    switch (this.action) {
      default:
      case ADD:
        NameTagWidget newCustomNameTag = new NameTagWidget("", CustomTag.createDefault());
        overlayWidget = this.initializeManageContainer(newCustomNameTag);
        break;
      case EDIT:
        overlayWidget = this.initializeManageContainer(selectedNameTag);
        break;
      case REMOVE:
        overlayWidget = this.initializeRemoveContainer(selectedNameTag);
        break;
    }

    manageContainer.addChild(overlayWidget);
    this.getDocument().addChild(manageContainer);
  }

  private FlexibleContentWidget initializeRemoveContainer(NameTagWidget nameTagWidget) {
    this.inputWidget = new FlexibleContentWidget(true);
    this.inputWidget.addId("remove-container");

    ComponentWidget confirmationWidget = ComponentWidget.i18n("nametags.gui.manage.remove.title");
    confirmationWidget.addId("remove-confirmation");
    this.inputWidget.addContent(confirmationWidget);

    NameTagWidget previewWidget = new NameTagWidget(nameTagWidget.getUserName(),
        nameTagWidget.getCustomTag());
    previewWidget.addId("remove-preview");
    this.inputWidget.addContent(previewWidget);

    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("remove-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.remove", () -> {
      this.nameTagConfiguration.getCustomTags().remove(nameTagWidget.getUserName());
      this.nameTagWidgets.remove(nameTagWidget.getUserName());
      this.nameTagList.getSession().setSelectedEntry(null);
      this.setAction(null);
    }));

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.cancel", () -> this.setAction(null)));
    this.inputWidget.addContent(menu);

    return this.inputWidget;
  }

  private DivWidget initializeManageContainer(NameTagWidget nameTagWidget) {
    DivWidget inputContainer = new DivWidget();
    inputContainer.addId("input-container");

    ComponentWidget customNameWidget = ComponentWidget.component(
        nameTagWidget.getCustomTag().getComponent());
    customNameWidget.addId("custom-preview");
    inputContainer.addChild(customNameWidget);

    this.inputWidget = new FlexibleContentWidget(true);
    this.inputWidget.addId("input-list");

    ComponentWidget labelName = ComponentWidget.i18n("nametags.gui.manage.name");
    labelName.addId("label-name");
    this.inputWidget.addContent(labelName);

    HorizontalListWidget nameList = new HorizontalListWidget();
    nameList.addId("input-name-list");

    IconWidget iconWidget = new IconWidget(
        nameTagWidget.getIconWidget(nameTagWidget.getUserName()));
    iconWidget.addId("input-avatar");
    nameList.addEntry(iconWidget);

    TextFieldWidget nameTextField = new TextFieldWidget();
    nameTextField.setText(nameTagWidget.getUserName());
    nameTextField.setUpdateListener(newValue -> {
      if (newValue.equals(this.lastUserName)) {
        return;
      }

      this.lastUserName = newValue;
      iconWidget.setIcon(nameTagWidget.getIconWidget(newValue));
    });

    nameList.addEntry(nameTextField);
    this.inputWidget.addContent(nameList);

    ComponentWidget labelCustomName = ComponentWidget.i18n("nametags.gui.manage.custom.name");
    labelCustomName.addId("label-name");
    this.inputWidget.addContent(labelCustomName);

    HorizontalListWidget customNameList = new HorizontalListWidget();
    customNameList.addId("input-name-list");

    DivWidget placeHolder = new DivWidget();
    placeHolder.addId("input-avatar");
    customNameList.addEntry(placeHolder);

    TextFieldWidget customTextField = new TextFieldWidget();
    customTextField.setText(nameTagWidget.getCustomTag().getCustomName());
    customTextField.setUpdateListener(newValue -> {
      if (newValue.equals(this.lastCustomName)) {
        return;
      }

      this.lastCustomName = newValue;
      customNameWidget.setComponent(
          LegacyComponentSerializer.legacyAmpersand().deserialize(newValue));
    });

    customNameList.addEntry(customTextField);
    this.inputWidget.addContent(customNameList);

    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("edit-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.done", () -> {
      if (nameTagWidget.getUserName().length() == 0) {
        this.nameTagWidgets.put(nameTextField.getText(), nameTagWidget);
        this.nameTagList.getSession().setSelectedEntry(nameTagWidget);
      }

      this.nameTagConfiguration.getCustomTags().remove(nameTagWidget.getUserName());
      CustomTag customTag = nameTagWidget.getCustomTag();
      customTag.setCustomName(customTextField.getText());
      this.nameTagConfiguration.getCustomTags().put(nameTextField.getText(), customTag);
      nameTagWidget.setUserName(nameTextField.getText());
      nameTagWidget.setCustomTag(customTag);
      this.setAction(null);
    }));

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.cancel", () -> this.setAction(null)));
    inputContainer.addChild(this.inputWidget);
    this.inputWidget.addContent(menu);
    return inputContainer;
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
    if (Objects.nonNull(this.action)) {
      return this.inputWidget.mouseClicked(mouseX, mouseY, mouseButton);
    }

    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean keyPressed(Key key, InputType type) {
    if (key.getId() == 256 && Objects.nonNull(this.action)) {
      this.setAction(null);
      return true;
    }

    return super.keyPressed(key, type);
  }

  private void setAction(Action action) {
    this.action = action;
    this.reload();
  }

  private enum Action {
    ADD, EDIT, REMOVE
  }
}
