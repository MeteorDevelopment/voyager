package meteordevelopment.voyager.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import meteordevelopment.voyager.utils.Color;
import meteordevelopment.voyager.utils.RenderPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {
    private final File file;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Settings(File file) {
        this.file = file;

        load();
    }

    // General
    private final String general = "General";
    public final Setting<String> prefix = add(new StringSetting(general, "prefix", "Prefix", "Chat commands prefix.", true, "-"));

    // Render
    private final String render = "Render";
    public final Setting<RenderPath> renderPath = add(new EnumSetting<>(render, "render-path", "Render path", "When to render path.", RenderPath.OnlyWhenMoving));
    public final Setting<Color> pathColor = add(new ColorSetting(render, "path-color", "Path color", "Color of the path.", new Color(25, 225, 25, 255)));

    // Debug
    private final String debug = "Debug";
    public final Setting<Boolean> chatDebug = add(new BoolSetting(debug, "chat-debug", "Chat debug", "Prints various debugging information to chat.", false));
    public final Setting<Boolean> renderPossibleMoves = add(new BoolSetting(debug, "render-possible-moves", "Render possible moves", "Renders all moves that Voyager will consider from you current position.", false));

    private <T> Setting<T> add(Setting<T> setting) {
        settings.add(setting);
        return setting;
    }

    public Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(new LiteralText("Voyager Config"))
                .setParentScreen(parent)
                .setSavingRunnable(this::save);

        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        Map<String, ConfigCategory> categories = new HashMap<>();

        for (Setting<?> setting : settings) {
            ConfigCategory category = categories.computeIfAbsent(setting.category, s -> builder.getOrCreateCategory(new LiteralText(s)));
            category.addEntry(setting.createConfigEntry(entryBuilder));
        }

        return builder.build();
    }

    public void save() {
        if (file == null) return;

        JsonObject json = new JsonObject();
        for (Setting<?> setting : settings) setting.write(json);

        try {
            file.getParentFile().mkdirs();

            JsonWriter writer = new JsonWriter(new FileWriter(file));
            writer.setIndent("  ");
            Streams.write(json, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (file == null || !file.exists()) return;

        try {
            JsonObject json = new JsonParser().parse(new FileReader(file)).getAsJsonObject();

            for (Setting<?> setting : settings) {
                JsonElement e = json.get(setting.name);
                if (e != null) setting.read(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
