package me.lortseam.completeconfig.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to declare that a field should be resolved as config entry, using the parameters specified in this
 * annotation.
 *
 * @see ConfigEntries
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigEntry {

    /**
     * Specifies the ID of this entry. If empty, the field name will be used by default.
     *
     * @return the ID
     */
    String value() default "";

    /**
     * Specifies a comment to describe the purpose of this entry. The comment will only be visible in the config
     * save file.
     *
     * <p>If blank, no comment will be applied to the entry.
     *
     * @return a comment
     */
    String comment() default "";

    /**
     * Specifies a custom translation key for this entry. If empty, the default key for this entry will be used.
     *
     * @return a custom translation key
     */
    String translationKey() default "";

    /**
     * Specifies one or more custom translation keys for this entry's tooltip, declared line by line. If empty, the
     * default single-line or multi-line keys will be used, depending on which are defined in the language file(s).
     *
     * @return an array of custom tooltip translation keys
     */
    String[] tooltipTranslationKeys() default {};

    /**
     * Specifies whether the game needs to be restarted after modifying the entry.
     *
     * @return whether the game needs to be restarted after modifying the entry
     */
    boolean requiresRestart() default false;

    /**
     * Applied to an entry of type Boolean.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Boolean {

        /**
         * A custom translation key for the value {@code true}. If empty, the default key will be used.
         *
         * @return a custom value translation key
         */
        String trueTranslationKey() default "";

        /**
         * A custom translation key for the value {@code false}. If empty, the default key will be used.
         *
         * @return a custom value translation key
         */
        String falseTranslationKey() default "";
        
    }

    /**
     * Applies bounds to an entry of type Integer.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BoundedInteger {

        /**
         * The lower bound (minimum).
         *
         * @return the lower bound
         */
        int min() default java.lang.Integer.MIN_VALUE;

        /**
         * The upper bound (maximum).
         *
         * @return the upper bound
         */
        int max() default java.lang.Integer.MAX_VALUE;

    }

    /**
     * Applies bounds to an entry of type Long.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BoundedLong {

        /**
         * The lower bound (minimum).
         *
         * @return the lower bound
         */
        long min() default java.lang.Long.MIN_VALUE;

        /**
         * The upper bound (maximum).
         *
         * @return the upper bound
         */
        long max() default java.lang.Long.MAX_VALUE;

    }

    /**
     * Applies bounds to an entry of type Float.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BoundedFloat {

        /**
         * The lower bound (minimum).
         *
         * @return the lower bound
         */
        float min() default -java.lang.Float.MAX_VALUE;

        /**
         * The upper bound (maximum).
         *
         * @return the upper bound
         */
        float max() default java.lang.Float.MAX_VALUE;

    }

    /**
     * Applies bounds to an entry of type Double.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BoundedDouble {

        /**
         * The lower bound (minimum).
         *
         * @return the lower bound
         */
        double min() default -java.lang.Double.MAX_VALUE;

        /**
         * The upper bound (maximum).
         *
         * @return the upper bound
         */
        double max() default java.lang.Double.MAX_VALUE;

    }

    /**
     * If applied, renders a slider for this entry. Usually used together with a bounding annotation.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Slider {

        /**
         * Specifies a custom translation key for this entry's value. If empty, the default key will be used.
         *
         * <p>The translation must contain a single format specifier of the entry's type, which will be replaced with
         * the actual value.
         *
         * @return a custom value translation key
         */
        String valueTranslationKey() default "";

    }

    /**
     * If applied, renders a dropdown menu for this entry.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Dropdown {

        /**
         * Specifies whether to make the value editable and show suggestions only.
         *
         * @return whether to enable suggestions
         */
        boolean suggestionMode() default false;

    }

    /**
     * Applied to an entry which represents a color.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Color {

        /**
         * Specifies whether the color has an alpha value.
         *
         * @return whether the color has an alpha value
         */
        boolean alphaMode();

    }

}
