package me.lortseam.completeconfig.data;

import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.io.ConfigSource;
import me.lortseam.completeconfig.util.ReflectionUtils;
import net.minecraft.text.TextColor;

import java.util.Collection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public final class Registry {

    private static final Map<String, ModConfigSet> configs = new HashMap<>();
    private static final Set<Entry> entries = new HashSet<>();
    private static final List<Transformation> transformations = Lists.newArrayList(
            Transformation.builder().byType(boolean.class, Boolean.class).byAnnotation(ConfigEntry.Boolean.class, true).transforms(BooleanEntry::new),
            Transformation.builder().byType(int.class, Integer.class).byAnnotation(ConfigEntry.BoundedInteger.class).transforms(origin -> {
                ConfigEntry.BoundedInteger bounds = origin.getAnnotation(ConfigEntry.BoundedInteger.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(int.class, Integer.class).byAnnotation(Arrays.asList(ConfigEntry.BoundedInteger.class, ConfigEntry.Slider.class)).transforms(origin -> {
                ConfigEntry.BoundedInteger bounds = origin.getAnnotation(ConfigEntry.BoundedInteger.class);
                return new SliderEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(long.class, Long.class).byAnnotation(ConfigEntry.BoundedLong.class).transforms(origin -> {
                ConfigEntry.BoundedLong bounds = origin.getAnnotation(ConfigEntry.BoundedLong.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(long.class, Long.class).byAnnotation(Arrays.asList(ConfigEntry.BoundedLong.class, ConfigEntry.Slider.class)).transforms(origin -> {
                ConfigEntry.BoundedLong bounds = origin.getAnnotation(ConfigEntry.BoundedLong.class);
                return new SliderEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(float.class, Float.class).byAnnotation(ConfigEntry.BoundedFloat.class).transforms(origin -> {
                ConfigEntry.BoundedFloat bounds = origin.getAnnotation(ConfigEntry.BoundedFloat.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(double.class, Double.class).byAnnotation(ConfigEntry.BoundedDouble.class).transforms(origin -> {
                ConfigEntry.BoundedDouble bounds = origin.getAnnotation(ConfigEntry.BoundedDouble.class);
                return new BoundedEntry<>(origin, bounds.min(), bounds.max());
            }),
            Transformation.builder().byType(type -> Enum.class.isAssignableFrom(ReflectionUtils.getTypeClass(type))).transforms(EnumEntry::new),
            Transformation.builder().byType(type -> Enum.class.isAssignableFrom(ReflectionUtils.getTypeClass(type))).byAnnotation(ConfigEntry.Dropdown.class).transforms(DropdownEntry::new),
            Transformation.builder().byAnnotation(ConfigEntry.Color.class).transforms(ColorEntry::new),
            Transformation.builder().byType(TextColor.class).transforms(origin -> new ColorEntry<>(origin, false))
    );

    static void register(Config config, boolean main) {
        getConfigs(config.getMod().getId()).add(config, main);
    }

    static void register(Entry<?> entry) {
        if (!entries.add(entry)) {
            throw new UnsupportedOperationException(entry + " was already resolved");
        }
    }

    static void register(Transformation... transformations) {
        Registry.transformations.addAll(Arrays.asList(transformations));
    }

    /**
     * Sets the main screen builder for a mod. The main screen builder will be used to build the config screen if no
     * custom builder was specified.
     *
     * @param modId the mod's ID
     * @param screenBuilder the screen builder
     */
    public static void register(@NonNull String modId, @NonNull ConfigScreenBuilder screenBuilder) {
        getConfigs(modId).screenBuilder = screenBuilder;
    }

    public boolean hasSource(ConfigSource source) {
        return getConfigs().stream().map(Config::getSource).collect(Collectors.toSet()).contains(source);
    }

    private static ModConfigSet getConfigs(String modId) {
        return configs.computeIfAbsent(modId, key -> new ModConfigSet());
    }

    static Set<Config> getConfigs() {
        return configs.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public static Optional<Config> getMainConfig(String modId) {
        return Optional.ofNullable(configs.get(modId)).map(modConfigs -> modConfigs.main);
    }

    public static Map<String, Config> getMainConfigs() {
        return configs.keySet().stream().map(Registry::getMainConfig).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toMap(config -> {
            return config.getMod().getId();
        }, Function.identity()));
    }

    static List<Transformation> getTransformations() {
        return Collections.unmodifiableList(transformations);
    }

    public static Optional<ConfigScreenBuilder> getScreenBuilder(String modId, ConfigScreenBuilder fallback) {
        ConfigScreenBuilder screenBuilder = getConfigs(modId).screenBuilder;
        if (screenBuilder != null) {
            return Optional.of(screenBuilder);
        }
        return Optional.ofNullable(fallback);
    }

    private static class ModConfigSet extends HashSet<Config> {

        private Config main;
        private ConfigScreenBuilder screenBuilder;

        private void add(Config config, boolean main) {
            add(config);
            if (main) {
                this.main = config;
            }
        }

    }

}
