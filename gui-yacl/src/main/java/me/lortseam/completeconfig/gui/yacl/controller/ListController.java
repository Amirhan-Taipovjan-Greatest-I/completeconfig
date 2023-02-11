package me.lortseam.completeconfig.gui.yacl.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.ListOptionEntry;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class ListController<T> implements Controller<List<T>> {

    @Getter
    final Function<ListOptionEntry<T>, Controller<T>> elementControllerBuilder;
    @Getter
    final T initialValue;

    public ListController(Function<ListOptionEntry<T>, Controller<T>> elementControllerBuilder) {
        this(elementControllerBuilder, null);
    }

    @Override
    public Option<List<T>> option() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Text formatValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        throw new UnsupportedOperationException();
    }

}
