package net.labymod.addons.customnametags.legacy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.labymod.addons.customnametags.CustomNameTag;
import net.labymod.addons.customnametags.CustomNameTags;
import net.labymod.addons.customnametags.CustomNameTagsConfiguration;
import net.labymod.api.configuration.converter.LegacyConverter;
import java.util.Map.Entry;

public class CustomNameTagsLegacyConverter extends LegacyConverter<JsonObject> {

  private final CustomNameTags addon;

  public CustomNameTagsLegacyConverter(CustomNameTags addon) {
    super("tags.json", JsonObject.class);

    this.addon = addon;
  }

  @Override
  protected void convert(JsonObject jsonObject) {
    CustomNameTagsConfiguration configuration = this.addon.configuration();

    JsonObject labymodSettings =
        this.fromJson(LEGACY_PATH.resolve("LabyMod-3.json"), JsonObject.class);

    configuration.enabled().set(
        labymodSettings != null
            && labymodSettings.has("tags")
            && labymodSettings.get("tags").getAsBoolean()
    );

    for (Entry<String, JsonElement> entry : jsonObject.get("tags").getAsJsonObject().entrySet()) {
      CustomNameTag tag = CustomNameTag.create(
          true,
          entry.getValue().getAsString(),
          true
      );

      configuration.getCustomTags().put(entry.getKey(), tag);
    }

    this.addon.saveConfiguration();
  }

  @Override
  public boolean hasStuffToConvert() {
    return this.getValue() != null
        && this.getValue().has("tags")
        && this.getValue().getAsJsonObject("tags").size() != 0;
  }
}
