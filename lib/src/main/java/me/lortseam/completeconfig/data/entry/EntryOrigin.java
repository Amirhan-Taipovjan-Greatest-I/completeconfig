package me.lortseam.completeconfig.data.entry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.data.text.TranslationIdentifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EntryOrigin {

    @Getter
    protected final Field field;
    @Getter
    private final ConfigContainer parentObject;
    @Getter
    private final TranslationIdentifier parentTranslation;

    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationType) {
        if (field.isAnnotationPresent(annotationType)) {
            return Optional.of(field.getAnnotation(annotationType));
        }
        return Optional.empty();
    }

}
