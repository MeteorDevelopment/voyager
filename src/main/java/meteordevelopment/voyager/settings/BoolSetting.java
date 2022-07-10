package meteordevelopment.voyager.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class BoolSetting extends Setting<Boolean> {
    public BoolSetting(String category, String name, String title, String description, boolean defaultValue) {
        super(category, name, title, description, defaultValue);
    }

    @Override
    public AbstractConfigListEntry<?> createConfigEntry(ConfigEntryBuilder builder) {
        return builder.startBooleanToggle(Text.of(title), get())
                .setTooltip(Text.of(description))
                .setDefaultValue(getDefaultValue())
                .setSaveConsumer(this::set)
                .build();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty(name, get());
    }

    @Override
    public void read(JsonElement e) {
        if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isBoolean()) return;

        set(e.getAsBoolean());
    }
}
