package net.labymod.addons.nametags.gui.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import net.labymod.addons.nametags.CustomTag;
import net.labymod.addons.nametags.NameTagConfiguration;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.VerticalListWidget;

public class NameTagActivity extends SimpleActivity {

  private final NameTagConfiguration nameTagConfiguration;
  private final VerticalListWidget<NameTagWidget> nameTagList;
  private final Map<Integer, TempNameTag> tempNameTags;

  private int selectedNameTag = -1;

  @Inject
  public NameTagActivity(NameTagConfiguration nameTagConfiguration) {
    this.nameTagConfiguration = nameTagConfiguration;
    this.tempNameTags = new HashMap<>();
    int i = 0;
    for (Entry<String, CustomTag> tagEntry : nameTagConfiguration.getCustomTags().entrySet()) {
      this.tempNameTags.put(i, new TempNameTag(i, tagEntry.getKey(), tagEntry.getValue()));
      i++;
    }

    this.addPlaceholder(false);

    this.nameTagList = new VerticalListWidget<>();
    this.nameTagList.addId("name-tag-list");
    this.nameTagList.setSelectCallback(this::onNameTagSelect);
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.renderBackground = false;

    DivWidget container = new DivWidget();
    container.addId("name-tag-container");

    boolean hasPlaceholder = false;
    for (TempNameTag value : this.tempNameTags.values()) {
      NameTagWidget widget = new NameTagWidget(value);
      if (widget.getTempNameTag().getId() == this.selectedNameTag) {
        widget.addId("name-tag-selected");
      }

      if (!hasPlaceholder && value.isPlaceholder()) {
        hasPlaceholder = true;
      }

      this.nameTagList.addChild(widget);
    }

    if (!hasPlaceholder) {
      this.addPlaceholder(true);
    }

    container.addChild(new ScrollWidget(this.nameTagList));
    this.getDocument().addChild(container);
  }

  private void onNameTagSelect(NameTagWidget nameTagWidget) {
    if (nameTagWidget.isMouseOverWidget()) {
      return;
    }

    if (nameTagWidget.getTempNameTag().getId() == this.selectedNameTag) {
      this.selectedNameTag = -1;
    } else {
      this.selectedNameTag = nameTagWidget.getTempNameTag().getId();
    }

    this.reload();
  }

  @Override
  public void onCloseScreen() {
    this.nameTagConfiguration.getCustomTags().clear();
    this.tempNameTags.values().forEach(tempNameTag -> this.nameTagConfiguration.getCustomTags().put(
        tempNameTag.getUserName(), tempNameTag.getCustomTag()));
    try {
      this.nameTagConfiguration.save();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void addPlaceholder(boolean add) {
    TempNameTag placeholder = new TempNameTag(this.tempNameTags.size(), "",
        new CustomTag(true, "", false));
    placeholder.setPlaceholder(true);
    this.tempNameTags.put(placeholder.getId(), placeholder);

    if (add) {
      this.nameTagList.addChild(new NameTagWidget(placeholder));
    }
  }
}
