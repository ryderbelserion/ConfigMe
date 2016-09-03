package com.github.authme.configme;

import com.github.authme.configme.migration.MigrationService;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.knownproperties.PropertyEntry;
import com.github.authme.configme.resource.PropertyResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.github.authme.configme.properties.PropertyInitializer.newProperty;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Test for {@link SettingsManager}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingsManagerTest {

    private final PropertyEntry[] propertyEntries = {
        new PropertyEntry(newProperty("demo.prop", 3)),
        new PropertyEntry(newProperty("demo.prop2", "test")),
        new PropertyEntry(newProperty("demo.prop3", 0))
    };

    @Mock
    private PropertyResource resource;

    @Mock
    private MigrationService migrationService;

    @Captor
    private ArgumentCaptor<List<PropertyEntry>> knownPropertiesCaptor;

    @Test
    public void shouldCheckMigrationServiceOnStartup() {
        // given
        List<PropertyEntry> knownProperties = Arrays.asList(propertyEntries);
        given(migrationService.checkAndMigrate(eq(resource), anyListOf(PropertyEntry.class))).willReturn(false);

        // when
        new SettingsManager(resource, migrationService, knownProperties);

        // then
        verifyWasMigrationServiceChecked();
        verifyZeroInteractions(resource);
    }

    @Test
    public void shouldSaveAfterPerformingMigrations() {
        // given
        List<PropertyEntry> knownProperties = Arrays.asList(propertyEntries);
        given(migrationService.checkAndMigrate(eq(resource), anyListOf(PropertyEntry.class))).willReturn(true);

        // when
        new SettingsManager(resource, migrationService, knownProperties);

        // then
        verifyWasMigrationServiceChecked();
        verify(resource).exportProperties(anyListOf(PropertyEntry.class));
    }

    @Test
    public void shouldGetProperty() {
        // given
        SettingsManager manager = createManager();
        Property<String> property = typedMock();
        String propValue = "Hello world";
        given(property.getValue(resource)).willReturn(propValue);

        // when
        String result = manager.getProperty(property);

        // then
        verify(property).getValue(resource);
        assertThat(result, equalTo(propValue));
    }

    @Test
    public void shouldSetProperty() {
        // given
        SettingsManager manager = createManager();
        Property<String> property = typedMock();
        String propertyPath = "property.path.test";
        given(property.getPath()).willReturn(propertyPath);
        String value = "Hello there";

        // when
        manager.setProperty(property, value);

        // then
        verify(resource).setValue(propertyPath, value);
    }

    @Test
    public void shouldPerformReload() {
        // given
        SettingsManager manager = createManager();
        given(migrationService.checkAndMigrate(eq(resource), anyListOf(PropertyEntry.class))).willReturn(false);

        // when
        manager.reload();

        // then
        verify(resource).reload();
        verifyWasMigrationServiceChecked();
    }

    private void verifyWasMigrationServiceChecked() {
        verify(migrationService, only()).checkAndMigrate(eq(resource), knownPropertiesCaptor.capture());
        assertThat(knownPropertiesCaptor.getValue(), contains(propertyEntries));
    }

    private SettingsManager createManager() {
        given(migrationService.checkAndMigrate(eq(resource), anyListOf(PropertyEntry.class))).willReturn(false);
        SettingsManager manager = new SettingsManager(resource, migrationService, Arrays.asList(propertyEntries));
        reset(migrationService);
        return manager;
    }

    @SuppressWarnings("unchecked")
    private static <T> Property<T> typedMock() {
        return mock(Property.class);
    }
}
