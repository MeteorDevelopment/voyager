package meteordevelopment.voyager.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

public abstract class Setting<T> {
    public final String category;
    public final String name, title, description;

    private final T defaultValue;
    private T value;

    public Setting(String category, String name, String title, String description, T defaultValue) {
        this.category = category;
        this.name = name;
        this.title = title;
        this.description = description;
        this.defaultValue = defaultValue;

        value = defaultValue;
    }

    public T get() {
        return value;
    }
    public void set(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public abstract AbstractConfigListEntry<?> createConfigEntry(ConfigEntryBuilder builder);

    public abstract void write(JsonObject json);

    public abstract void read(JsonElement e);
}
