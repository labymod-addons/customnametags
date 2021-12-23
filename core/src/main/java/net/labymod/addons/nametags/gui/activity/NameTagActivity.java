package net.labymod.addons.nametags.gui.activity;

import javax.inject.Inject;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.activity.types.extension.ListExtension;
import net.labymod.api.client.gui.screen.activity.types.extension.NavigationExtension;

public class NameTagActivity extends SimpleActivity implements NavigationExtension, ListExtension {

  @Inject
  public NameTagActivity() {
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
  }

  @Override
  public void initializeExtension() {
    NavigationExtension.super.initializeExtension();
    ListExtension.super.initializeExtension();
  }
}
