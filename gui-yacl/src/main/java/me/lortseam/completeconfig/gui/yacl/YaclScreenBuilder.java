package me.lortseam.completeconfig.gui.yacl;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.ColorController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import dev.isxander.yacl.gui.controllers.string.number.DoubleFieldController;
import dev.isxander.yacl.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import dev.isxander.yacl.gui.controllers.string.number.LongFieldController;
import me.lortseam.completeconfig.CompleteConfig;
import me.lortseam.completeconfig.data.*;
import me.lortseam.completeconfig.gui.ConfigScreenBuilder;
import me.lortseam.completeconfig.gui.GuiProvider;
import me.lortseam.completeconfig.gui.yacl.controller.ListController;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * A screen builder based on the YetAnotherConfigLib library.
 */
public final class YaclScreenBuilder extends ConfigScreenBuilder<ControllerFunction<?>> {

    private static final List<GuiProvider<ControllerFunction<?>>> globalProviders = Lists.newArrayList(
            GuiProvider.create(BooleanEntry.class, entry -> (Option<Boolean> option) -> new BooleanController(
                    option,
                    entry.getValueFormatter(),
                    false
            ), (BooleanEntry entry) -> !entry.isCheckbox(), boolean.class, Boolean.class),
            GuiProvider.create(BooleanEntry.class, entry -> (Option<Boolean> option) -> new TickBoxController(
                    option
            ), BooleanEntry::isCheckbox, boolean.class, Boolean.class),
            GuiProvider.create((Entry<Integer> entry) -> (Option<Integer> option) -> new IntegerFieldController(
                    option,
                    entry.getValueFormatter()
            ), int.class, Integer.class),
            GuiProvider.create((Entry<Long> entry) -> (Option<Long> option) -> new LongFieldController(
                    option,
                    entry.getValueFormatter()
            ), long.class, Long.class),
            GuiProvider.create((Entry<Float> entry) -> (Option<Float> option) -> new FloatFieldController(
                    option,
                    entry.getValueFormatter()
            ), float.class, Float.class),
            GuiProvider.create((Entry<Double> entry) -> (Option<Double> option) -> new DoubleFieldController(
                    option,
                    entry.getValueFormatter()
            ), double.class, Double.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Integer> entry) -> (Option<Integer> option) -> new IntegerFieldController(
                    option,
                    entry.getMin(),
                    entry.getMax(),
                    entry.getValueFormatter()
            ), int.class, Integer.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Long> entry) -> (Option<Long> option) -> new LongFieldController(
                    option,
                    entry.getMin(),
                    entry.getMax(),
                    entry.getValueFormatter()
            ), long.class, Long.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Float> entry) -> (Option<Float> option) -> new FloatFieldController(
                    option,
                    entry.getMin(),
                    entry.getMax(),
                    entry.getValueFormatter()
            ), float.class, Float.class),
            GuiProvider.create(BoundedEntry.class, (BoundedEntry<Double> entry) -> (Option<Double> option) -> new DoubleFieldController(
                    option,
                    entry.getMin(),
                    entry.getMax(),
                    entry.getValueFormatter()
            ), double.class, Double.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Integer> entry) -> (Option<Integer> option) -> new IntegerSliderController(
                    option,
                    entry.getMin(),
                    entry.getMax(),
                    entry.getInterval().orElse(1)
            ), int.class, Integer.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Long> entry) -> (Option<Long> option) -> new LongSliderController(
                    option,
                    entry.getMin(),
                    entry.getMax(),
                    entry.getInterval().orElse(1L)
            ), long.class, Long.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Float> entry) -> (Option<Float> option) -> new FloatSliderController(
                    option,
                    entry.getMin(),
                    entry.getMax(),
                    entry.getInterval().orElse(0.1f)
            ), float.class, Float.class),
            GuiProvider.create(SliderEntry.class, (SliderEntry<Double> entry) -> (Option<Double> option) -> new DoubleSliderController(
                    option,
                    entry.getMin(),
                    entry.getMax(),
                    entry.getInterval().orElse(0.01)
            ), double.class, Double.class),
            GuiProvider.create(entry -> (Option<String> option) -> new StringController(
                    option
            ), String.class),
            GuiProvider.create(EnumEntry.class, (EnumEntry<?> entry) -> (Option<Enum<?>> option) -> new EnumController(
                    option,
                    entry.getValueFormatter()
            )),
            GuiProvider.create(ColorEntry.class, (ColorEntry<Color> entry) -> (Option<Color> option) -> new ColorController(
                    option,
                    entry.isAlphaMode()
            ), Color.class),
            GuiProvider.create((Entry<List<Integer>> entry) -> (Option<List<Integer>> listOption) -> new ListController<>(
                    IntegerFieldController::new
            ), new TypeToken<List<Integer>>() {}.getType())
    );

    static {
        for (Collection<GuiProvider<ControllerFunction<?>>> providers : CompleteConfig.collectExtensions(YaclGuiExtension.class, YaclGuiExtension::getProviders)) {
            globalProviders.addAll(providers);
        }
    }

    public YaclScreenBuilder() {
        super(globalProviders);
    }

    @Override
    public Screen build(Screen parentScreen, Config config) {
        var configBuilder = YetAnotherConfigLib.createBuilder()
                .title(getTitle(config))
                .save(config::save);
        if (!config.getEntries().isEmpty()) {
            var categoryBuilder = ConfigCategory.createBuilder()
                    .name(config.getName());
            for (Entry<?> entry : config.getEntries()) {
                categoryBuilder.option(buildOption(entry));
            }
            configBuilder.category(categoryBuilder.build());
        }
        for (var cluster : config.getClusters()) {
            var categoryBuilder = ConfigCategory.createBuilder()
                    .name(cluster.getName());
            cluster.getDescription().ifPresent(categoryBuilder::tooltip);
            for (Entry<?> entry : cluster.getEntries()) {
                categoryBuilder.option(buildOption(entry));
            }
            for (var subCluster : cluster.getClusters()) {
                var groupBuilder = OptionGroup.createBuilder()
                        .name(subCluster.getName());
                subCluster.getDescription().ifPresent(groupBuilder::tooltip);
                for (Entry<?> entry : subCluster.getEntries()) {
                    groupBuilder.option(buildOption(entry));
                }
                if (!subCluster.getClusters().isEmpty()) {
                    throw new UnsupportedOperationException("YACL screen builder doesn't support more than 2 levels of groups");
                }
                categoryBuilder.group(groupBuilder.build());
            }
            configBuilder.category(categoryBuilder.build());
        }
        return configBuilder.build().generateScreen(parentScreen);
    }

    private <T> Option<T> buildOption(Entry<T> entry) {
        Controller<T> parentController = null;
        try {
            parentController = (Controller<T>) createEntry(entry).apply(null);
        } catch (NullPointerException ignore) {}
        if (parentController instanceof ListController<?>) {
            return buildListOption((Entry) entry, (ListController) parentController);
        }
        var builder = Option.createBuilder(entry.getTypeClass())
                .name(entry.getName())
                .binding(entry.getDefaultValue(), entry::getValue, entry::setValue)
                .controller(option -> ((ControllerFunction<T>) createEntry(entry)).apply(option));
        entry.getDescription().ifPresent(builder::tooltip);
        if (entry.requiresRestart()) {
            builder.flag(OptionFlag.GAME_RESTART);
        }
        return builder.build();
    }

    private <E, T extends List<E>> Option<T> buildListOption(Entry<T> entry, ListController<E> controller) {
        Class<E> elementClass = (Class<E>) entry.getGenericTypes()[0];
        E initialValue = controller.getInitialValue();
        // TODO: Add @ConfigEntry.List#initialValue for custom initial values
        if (initialValue == null) {
            if (elementClass == Boolean.class) {
                initialValue = (E) Boolean.FALSE;
            } else if (elementClass == Integer.class) {
                initialValue = (E) Integer.valueOf(0);
            } else if (elementClass == Long.class) {
                initialValue = (E) Long.valueOf(0);
            } else if (elementClass == Float.class) {
                initialValue = (E) Float.valueOf(0);
            } else if (elementClass == Double.class) {
                initialValue = (E) Double.valueOf(0);
            } else if (elementClass == String.class) {
                initialValue = (E) "";
            }
        }
        var builder = ListOption.createBuilder(elementClass)
                .name(entry.getName())
                .binding(entry.getDefaultValue(), entry::getValue, (value) -> entry.setValue((T) value))
                .controller(controller.getElementControllerBuilder())
                .initial(initialValue);
        entry.getDescription().ifPresent(builder::tooltip);
        if (entry.requiresRestart()) {
            builder.flag(OptionFlag.GAME_RESTART);
        }
        return (Option<T>) builder.build();
    }

}
