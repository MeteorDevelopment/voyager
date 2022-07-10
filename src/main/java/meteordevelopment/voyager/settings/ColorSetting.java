package meteordevelopment.voyager.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import meteordevelopment.voyager.utils.Color;
import net.minecraft.text.Text;

public class ColorSetting extends Setting<Color> {
    public ColorSetting(String category, String name, String title, String description, Color defaultValue) {
        super(category, name, title, description, defaultValue);
    }

    @Override
    public AbstractConfigListEntry<?> createConfigEntry(ConfigEntryBuilder builder) {
        return builder.startAlphaColorField(Text.of(title), get().pack())
                .setTooltip(Text.of(description))
                .setDefaultValue(get().pack())
                .setSaveConsumer(integer -> set(new Color(integer)))
                .build();
    }

    @Override
    public void write(JsonObject json) {
        JsonArray array = new JsonArray();

        array.add(get().r);
        array.add(get().g);
        array.add(get().b);
        array.add(get().a);

        json.add(name, array);
    }

    @Override
    public void read(JsonElement e) {
        if (!e.isJsonArray()) return;

        JsonArray array = e.getAsJsonArray();
        if (array.size() != 4) return;

        set(new Color(
                array.get(0).getAsInt(),
                array.get(1).getAsInt(),
                array.get(2).getAsInt(),
                array.get(3).getAsInt()
        ));
    }
}
