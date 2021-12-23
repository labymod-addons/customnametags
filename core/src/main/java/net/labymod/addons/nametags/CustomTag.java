package net.labymod.addons.nametags;

public class CustomTag {

  private String userName;
  private String customName;

  private CustomTag(String userName, String customName) {
    this.userName = userName;
    this.customName = customName;
  }

  public static CustomTag create(String userName, String customName) {
    return new CustomTag(userName, customName);
  }

  public String getUserName() {
    return this.userName;
  }

  public String getCustomName() {
    return this.customName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setCustomName(String customName) {
    this.customName = customName;
  }
}
