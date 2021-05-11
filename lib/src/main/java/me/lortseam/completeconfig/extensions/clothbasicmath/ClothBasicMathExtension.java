package me.lortseam.completeconfig.extensions.clothbasicmath;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.lortseam.completeconfig.data.ColorEntry;
import me.lortseam.completeconfig.data.entry.Transformation;
import me.lortseam.completeconfig.extensions.CompleteConfigExtension;
import me.lortseam.completeconfig.extensions.Extension;
import me.shedaniel.math.Color;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClothBasicMathExtension implements CompleteConfigExtension {

    @Override
    public TypeSerializerCollection getTypeSerializers() {
        return TypeSerializerCollection.builder()
                .registerExact(ColorSerializer.INSTANCE)
                .build();
    }

    @Override
    public Transformation[] getTransformations() {
        return new Transformation[] {
                Transformation.builder().byType(Color.class).transforms(origin -> new ColorEntry<>(origin, true))
        };
    }

    @Override
    public Set<Class<? extends Extension>> children() {
        return ImmutableSet.of(ClothBasicMathGuiExtension.class);
    }

}
