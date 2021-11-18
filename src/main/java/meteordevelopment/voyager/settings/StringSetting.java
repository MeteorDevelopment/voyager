package meteordevelopment.voyager.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Optional;

public class StringSetting extends Setting<String> {
    private final boolean oneCharacter;

    public StringSetting(String category, String name, String title, String description, boolean oneCharacter, String defaultValue) {
        super(category, name, title, description, defaultValue);

        this.oneCharacter = oneCharacter;
    }

    private Optional<Text> getError(String s) {
        if (s.isEmpty()) return Optional.of(new LiteralText(title + " cannot be empty."));
        if (oneCharacter && (s.length() > 1 || Character.isWhitespace(s.charAt(0)))) return Optional.of(new LiteralText(title + " can only be a single non-whitespace character."));

        return Optional.empty();
    }

    @Override
    public AbstractConfigListEntry<?> createConfigEntry(ConfigEntryBuilder builder) {
        return builder.startTextField(new LiteralText(title), get())
                .setTooltip(new LiteralText(description))
                .setDefaultValue(getDefaultValue())
                .setSaveConsumer(this::set)
                .setErrorSupplier(this::getError)
                .build();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty(name, get());
    }

    @Override
    public void read(JsonElement e) {
        if (!e.isJsonPrimitive() && !e.getAsJsonPrimitive().isString()) return;

        set(e.getAsString());
    }
}
