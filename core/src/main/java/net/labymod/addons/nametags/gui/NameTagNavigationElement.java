package net.labymod.addons.nametags.gui;

import javax.inject.Inject;
import net.kyori.adventure.text.Component;
import net.labymod.addons.nametags.gui.activity.NameTagActivity;
import net.labymod.api.client.gui.navigation.elements.ActivityNavigationElement;
import net.labymod.api.client.gui.screen.activity.Activity;

public class NameTagNavigationElement extends ActivityNavigationElement {

  @Inject
  private NameTagNavigationElement() {
  }

  @Override
  public Class<? extends Activity> getActivityClass() {
    return NameTagActivity.class;
  }

  @Override
  public String getWidgetWrapperId() {
    return "nametag-navigation-wrapper";
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("nametags.gui.navigation.name");
  }
}
