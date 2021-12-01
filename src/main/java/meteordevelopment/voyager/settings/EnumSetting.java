package meteordevelopment.voyager.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.LiteralText;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {
    private final Class<T> klass;

    @SuppressWarnings("unchecked")
    public EnumSetting(String category, String name, String title, String description, T defaultValue) {
        super(category, name, title, description, defaultValue);
        klass = (Class<T>) defaultValue.getClass();
    }

    @Override
    public AbstractConfigListEntry<?> createConfigEntry(ConfigEntryBuilder builder) {
        return builder.startEnumSelector(new LiteralText(title), klass, get())
                .setTooltip(new LiteralText(description))
                .setDefaultValue(getDefaultValue())
                .setSaveConsumer(this::set)
                .build();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty(name, get().name());
    }

    @Override
    public void read(JsonElement e) {
        if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString()) return;

        try {
            set(Enum.valueOf(klass, e.getAsString()));
        }
        catch (IllegalArgumentException ignored) {}
    }
}
