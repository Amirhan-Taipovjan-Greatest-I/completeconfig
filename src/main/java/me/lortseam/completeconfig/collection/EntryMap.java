package me.lortseam.completeconfig.collection;

import com.google.common.base.CaseFormat;
import me.lortseam.completeconfig.ConfigMap;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigEntryContainer;
import me.lortseam.completeconfig.api.ConfigEntryListener;
import me.lortseam.completeconfig.entry.Entry;
import me.lortseam.completeconfig.entry.EnumOptions;
import me.lortseam.completeconfig.exception.IllegalAnnotationParameterException;
import me.lortseam.completeconfig.exception.IllegalAnnotationTargetException;
import me.lortseam.completeconfig.exception.IllegalModifierException;
import me.lortseam.completeconfig.exception.IllegalReturnTypeException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class EntryMap extends ConfigMap<Entry> {

    EntryMap(String modTranslationKey) {
        super(modTranslationKey);
    }

    void fill(Collection parent, ConfigEntryContainer container) {
        LinkedHashMap<String, Entry> containerEntries = new LinkedHashMap<>();
        for (Class<? extends ConfigEntryContainer> clazz : container.getConfigClasses()) {
            Arrays.stream(clazz.getDeclaredMethods()).filter(method -> !Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(ConfigEntryListener.class)).forEach(method -> {
                ConfigEntryListener listener = method.getDeclaredAnnotation(ConfigEntryListener.class);
                String fieldName = listener.value();
                if (fieldName.equals("")) {
                    if (!method.getName().startsWith("set") || method.getName().equals("set")) {
                        throw new IllegalAnnotationParameterException("Could not detect field name for listener method " + method + ", please provide it inside the annotation");
                    }
                    fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, method.getName().replaceFirst("set", ""));
                }
                Class<? extends ConfigEntryContainer> fieldClass = listener.container();
                if (fieldClass == ConfigEntryContainer.class) {
                    fieldClass = clazz;
                }
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Listener method " + method + " has wrong number of parameters");
                }
                Entry<?> entry = Entry.of(parent, fieldName, fieldClass);
                if (method.getParameterTypes()[0] != entry.getType()) {
                    throw new IllegalArgumentException("Listener method " + method + " has wrong argument type");
                }
                if (method.getReturnType() != Void.TYPE) {
                    throw new IllegalReturnTypeException("Listener method " + method + " may not return a type other than void");
                }
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                entry.addListener(method, container);
            });
            LinkedHashMap<String, Entry> clazzEntries = new LinkedHashMap<>();
            Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
                if (Modifier.isStatic(field.getModifiers())) {
                    return false;
                }
                if (container.isConfigPOJO()) {
                    return !ConfigEntryContainer.class.isAssignableFrom(field.getType()) && !field.isAnnotationPresent(ConfigEntry.Ignore.class);
                }
                return field.isAnnotationPresent(ConfigEntry.class);
            }).forEach(field -> {
                if (Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalModifierException("Entry field " + field + " must not be final");
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Entry<?> entry = Entry.of(parent, field, container);
                if (field.isAnnotationPresent(ConfigEntry.class)) {
                    ConfigEntry entryAnnotation = field.getDeclaredAnnotation(ConfigEntry.class);
                    String customTranslationKey = entryAnnotation.customTranslationKey();
                    if (!StringUtils.isBlank(customTranslationKey)) {
                        entry.setCustomTranslationKey(modTranslationKey + "."  + customTranslationKey);
                    }
                    String[] customTooltipKeys = entryAnnotation.customTooltipKeys();
                    if (customTooltipKeys.length > 0) {
                        for (String key : customTooltipKeys) {
                            if (StringUtils.isBlank(key)) {
                                throw new IllegalAnnotationParameterException("Tooltip key(s) of entry field " + field + " must not be blank");
                            }
                        }
                        entry.setCustomTooltipTranslationKeys(Arrays.stream(customTooltipKeys).map(key -> modTranslationKey + "." + key).toArray(String[]::new));
                    }
                    entry.setForceUpdate(entryAnnotation.forceUpdate());
                    entry.setRequiresRestart(entryAnnotation.requiresRestart());
                }
                if (field.isAnnotationPresent(ConfigEntry.Bounded.Integer.class)) {
                    if (field.getType() != int.class && field.getType() != Integer.class) {
                        throw new IllegalAnnotationTargetException("Cannot apply Integer bound to non Integer field " + field);
                    }
                    ConfigEntry.Bounded.Integer bounds = field.getDeclaredAnnotation(ConfigEntry.Bounded.Integer.class);
                    entry.setBounds(bounds.min(), bounds.max(), bounds.slider());
                }
                if (field.isAnnotationPresent(ConfigEntry.Bounded.Long.class)) {
                    if (field.getType() != long.class && field.getType() != Long.class) {
                        throw new IllegalAnnotationTargetException("Cannot apply Long bound to non Long field " + field);
                    }
                    ConfigEntry.Bounded.Long bounds = field.getDeclaredAnnotation(ConfigEntry.Bounded.Long.class);
                    entry.setBounds(bounds.min(), bounds.max(), bounds.slider());
                }
                if (field.isAnnotationPresent(ConfigEntry.Bounded.Float.class)) {
                    if (field.getType() != float.class && field.getType() != Float.class) {
                        throw new IllegalAnnotationTargetException("Cannot apply Float bound to non Float field " + field);
                    }
                    ConfigEntry.Bounded.Float bounds = field.getDeclaredAnnotation(ConfigEntry.Bounded.Float.class);
                    entry.setBounds(bounds.min(), bounds.max(), false);
                }
                if (field.isAnnotationPresent(ConfigEntry.Bounded.Double.class)) {
                    if (field.getType() != double.class && field.getType() != Double.class) {
                        throw new IllegalAnnotationTargetException("Cannot apply Double bound to non Double field " + field);
                    }
                    ConfigEntry.Bounded.Double bounds = field.getDeclaredAnnotation(ConfigEntry.Bounded.Double.class);
                    entry.setBounds(bounds.min(), bounds.max(), false);
                }
                if (Enum.class.isAssignableFrom(field.getType())) {
                    if (field.isAnnotationPresent(ConfigEntry.EnumOptions.class)) {
                        entry.setEnumOptions(field.getDeclaredAnnotation(ConfigEntry.EnumOptions.class).displayType());
                    } else {
                        entry.setEnumOptions(EnumOptions.DisplayType.getDefault());
                    }
                } else if (field.isAnnotationPresent(ConfigEntry.EnumOptions.class)) {
                    throw new IllegalAnnotationTargetException("Cannot apply enum options to non enum field " + field);
                }
                clazzEntries.put(field.getName(), entry);
            });
            //TODO: Quite hacky solution to sort the entries (superclasses first)
            clazzEntries.putAll(containerEntries);
            containerEntries = clazzEntries;
        }
        putAll(containerEntries);
    }

}
