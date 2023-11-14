package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.internal.record.RecordComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Factory which analyzes a class and returns all writable properties.
 * <p>
 * Default implementation: {@link BeanDescriptionFactoryImpl}.
 */
public interface BeanDescriptionFactory {

    @NotNull List<FieldProperty> getAllProperties(@NotNull Class<?> clazz);

    @NotNull List<FieldProperty> createRecordProperties(@NotNull Class<?> clazz,
                                                        RecordComponent @NotNull [] components);

}
