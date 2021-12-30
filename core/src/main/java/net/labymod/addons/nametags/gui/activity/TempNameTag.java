package net.labymod.addons.nametags.gui.activity;

import net.labymod.addons.nametags.CustomTag;

public class TempNameTag {

  private final int id;
  private final String defaultUserName;
  private final String defaultCustomName;
  private final boolean defaultEnabled;
  private final CustomTag customTag;
  private String userName;
  private boolean placeholder;

  protected TempNameTag(int id, String userName, CustomTag customTag) {
    this.id = id;
    this.userName = userName;
    this.customTag = new CustomTag(customTag.isEnabled(), customTag.getCustomName(),
        customTag.isReplaceScoreboard());

    this.defaultUserName = userName;
    this.defaultCustomName = customTag.getCustomName();
    this.defaultEnabled = customTag.isEnabled();
  }

  public int getId() {
    return this.id;
  }

  public String getDefaultUserName() {
    return this.defaultUserName;
  }

  public String getDefaultCustomName() {
    return this.defaultCustomName;
  }

  public boolean isDefaultEnabled() {
    return this.defaultEnabled;
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

  public boolean isPlaceholder() {
    return this.placeholder && !this.isChanged();
  }

  public void setPlaceholder(boolean placeholder) {
    this.placeholder = placeholder;
  }

  public boolean isChanged() {
    return !this.defaultUserName.equals(this.userName)
        || this.defaultEnabled != this.customTag.isEnabled()
        || !this.defaultCustomName.equals(this.customTag.getCustomName());
  }
}
