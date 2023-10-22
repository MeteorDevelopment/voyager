package meteordevelopment.voyager;

import meteordevelopment.meteorclient.pathing.IPathManager;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.voyager.goals.DirectionGoal;
import meteordevelopment.voyager.goals.XYZGoal;
import meteordevelopment.voyager.goals.XZGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class PathManager implements IPathManager {
    private final VoyagerSettings settings = new VoyagerSettings();

    @Override
    public String getName() {
        return "Voyager";
    }

    @Override
    public boolean isPathing() {
        return Voyager.INSTANCE.isMoving();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void stop() {
        Voyager.INSTANCE.stop();
    }

    @Override
    public void moveTo(BlockPos pos, boolean ignoreY) {
        if (ignoreY) {
            Voyager.INSTANCE.moveTo(new XZGoal(pos.getX(), pos.getY()));
            return;
        }

        Voyager.INSTANCE.moveTo(new XYZGoal(pos));
    }

    @Override
    public void moveInDirection(float yaw) {
        Voyager.INSTANCE.moveTo(new DirectionGoal(yaw));
    }

    @Override
    public void mine(Block... blocks) {
    }

    @Override
    public void follow(Predicate<Entity> predicate) {
        for (Entity entity : Voyager.mc.world.getEntities()) {
            if (predicate.test(entity)) {
                Voyager.INSTANCE.moveTo(new XYZGoal(entity.getBlockPos()));
                return;
            }
        }
    }

    @Override
    public float getTargetYaw() {
        if (Voyager.mc.player.input instanceof VInput input)
            return input.getYaw(1);

        return 0;
    }

    @Override
    public float getTargetPitch() {
        return isPathing() ? Voyager.mc.player.getPitch() : 0;
    }

    @Override
    public ISettings getSettings() {
        return settings;
    }

    private static class VoyagerSettings implements ISettings {
        private final Settings settings = new Settings();
        private final Setting<Boolean> setting = new BoolSetting.Builder().build();

        @SuppressWarnings({"rawtypes", "unchecked"})
        public VoyagerSettings() {
            Map<String, SettingGroup> groups = new HashMap<>();

            for (var setting : Voyager.INSTANCE.getSettings().settings) {
                SettingGroup group = groups.computeIfAbsent(setting.category, settings::createGroup);

                if (setting instanceof meteordevelopment.voyager.settings.BoolSetting s) {
                    group.add(new BoolSetting.Builder()
                            .name(s.name)
                            .description(s.description)
                            .defaultValue(s.getDefaultValue())
                            .onChanged(s::set)
                            .onModuleActivated(booleanSetting -> booleanSetting.set(s.get()))
                            .build());
                }
                else if (setting instanceof meteordevelopment.voyager.settings.EnumSetting s) {
                    group.add(new EnumSetting.Builder<>()
                            .name(s.name)
                            .description(s.description)
                            .defaultValue((Enum<?>) s.getDefaultValue())
                            .onChanged(anEnum -> s.set(anEnum))
                            .onModuleActivated(booleanSetting -> booleanSetting.set((Enum<?>) s.get()))
                            .build());
                }
                else if (setting instanceof meteordevelopment.voyager.settings.StringSetting s) {
                    group.add(new StringSetting.Builder()
                            .name(s.name)
                            .description(s.description)
                            .defaultValue(s.getDefaultValue())
                            .onChanged(s::set)
                            .onModuleActivated(stringSetting -> stringSetting.set(s.get()))
                            .build());
                }
                else if (setting instanceof meteordevelopment.voyager.settings.ColorSetting s) {
                    group.add(new ColorSetting.Builder()
                            .name(s.name)
                            .description(s.description)
                            .defaultValue(new Color(s.getDefaultValue().pack()))
                            .onChanged(settingColor -> s.set(new meteordevelopment.voyager.utils.Color(settingColor.getPacked())))
                            .onModuleActivated(settingColorSetting -> settingColorSetting.set(new SettingColor(s.get().pack())))
                            .build());
                }
                else {
                    throw new NotImplementedException();
                }
            }
        }

        @Override
        public Settings get() {
            return settings;
        }

        @Override
        public Setting<Boolean> getWalkOnWater() {
            return setting;
        }

        @Override
        public Setting<Boolean> getWalkOnLava() {
            return setting;
        }

        @Override
        public Setting<Boolean> getStep() {
            return setting;
        }

        @Override
        public Setting<Boolean> getNoFall() {
            return setting;
        }

        @Override
        public void save() {
            Voyager.INSTANCE.getSettings().save();
        }
    }
}
