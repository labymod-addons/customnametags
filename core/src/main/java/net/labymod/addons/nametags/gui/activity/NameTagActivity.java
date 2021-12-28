package net.labymod.addons.nametags.gui.activity;

import net.labymod.addons.nametags.CustomTag;
import net.labymod.addons.nametags.NameTagConfiguration;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.VerticalListWidget;
import javax.inject.Inject;
import java.util.Map.Entry;

public class NameTagActivity extends SimpleActivity {

  private final NameTagConfiguration nameTagConfiguration;

  private VerticalListWidget<NameTagWidget> nameTagList;
  private NameTagWidget selectedNameTag;

  @Inject
  public NameTagActivity(NameTagConfiguration nameTagConfiguration) {
    this.nameTagConfiguration = nameTagConfiguration;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    DivWidget container = new DivWidget();
    container.addId("name-tag-container");

    this.nameTagList = new VerticalListWidget<>();
    this.nameTagList.addId("name-tag-list");
    for (Entry<String, CustomTag> tagEntry : this.nameTagConfiguration.getCustomTags().entrySet()) {
      NameTagWidget widget = new NameTagWidget(tagEntry.getKey(), tagEntry.getValue());
      this.nameTagList.addChild(widget);
    }

    container.addChild(new ScrollWidget(this.nameTagList));
    this.getDocument().addChild(container);

    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("name-tag-button-menu");
    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.add"));
    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.edit"));
    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.remove"));
    this.getDocument().addChild(menu);
  }
}
