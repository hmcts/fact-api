package uk.gov.hmcts.dts.fact.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.i18n.LocaleContextHolder;
import uk.gov.hmcts.dts.fact.entity.OpeningType;

import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OpeningTimeTest {

    private static final String OPENING_HOURS = "9 to 5";
    private static final String DESCRIPTION = "description";
    private static final String DESCRIPTION_CY = "description cy";
    private static final String ADMIN_DESCRIPTION = "admin description";
    private static final String ADMIN_DESCRIPTION_CY = "admin description cy";

    private static uk.gov.hmcts.dts.fact.entity.OpeningTime entity;

    @BeforeAll
    static void setUp() {
        entity = new uk.gov.hmcts.dts.fact.entity.OpeningTime();
        entity.setId(117);
        entity.setDescription("A type");
        entity.setDescriptionCy("A type but in Welsh");
        entity.setHours("A set of hours");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void testCreation(boolean welsh) {
        if (welsh) {
            Locale locale = new Locale("cy");
            LocaleContextHolder.setLocale(locale);
        }
        OpeningTime openingTime = new OpeningTime(entity);
        assertEquals(entity.getHours(), openingTime.getHours());
        assertEquals(welsh ? entity.getDescriptionCy() : entity.getDescription(), openingTime.getType());

        LocaleContextHolder.resetLocaleContext();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForGetDescription() {
        return Stream.of(
            Arguments.of(DESCRIPTION, ADMIN_DESCRIPTION, ADMIN_DESCRIPTION),
            Arguments.of(null, ADMIN_DESCRIPTION, ADMIN_DESCRIPTION),
            Arguments.of(DESCRIPTION, null, DESCRIPTION)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetDescription")
    void testGetDescription(final String description, final String adminDescription, final String expectedDescription) {
        final OpeningType openingType = (adminDescription == null)
            ? null
            : new OpeningType(1, adminDescription, ADMIN_DESCRIPTION_CY);

        final uk.gov.hmcts.dts.fact.entity.OpeningTime openingTime = new uk.gov.hmcts.dts.fact.entity.OpeningTime(openingType, OPENING_HOURS);
        openingTime.setDescription(description);
        openingTime.setDescriptionCy(DESCRIPTION_CY);

        assertThat(openingTime.getDescription(openingTime)).isEqualTo(expectedDescription);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> parametersForGetDescriptionCy() {
        return Stream.of(
            Arguments.of(DESCRIPTION_CY, ADMIN_DESCRIPTION_CY, ADMIN_DESCRIPTION_CY),
            Arguments.of(null, ADMIN_DESCRIPTION_CY, ADMIN_DESCRIPTION_CY),
            Arguments.of(DESCRIPTION_CY, null, DESCRIPTION_CY)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetDescriptionCy")
    void testGetDescriptionCy(final String descriptionCy, final String adminDescriptionCy, final String expectedDescription) {
        final OpeningType openingType = (adminDescriptionCy == null)
            ? null
            : new OpeningType(1, ADMIN_DESCRIPTION, adminDescriptionCy);

        final uk.gov.hmcts.dts.fact.entity.OpeningTime openingTime = new uk.gov.hmcts.dts.fact.entity.OpeningTime(openingType, OPENING_HOURS);
        openingTime.setDescription(DESCRIPTION);
        openingTime.setDescriptionCy(descriptionCy);

        assertThat(openingTime.getDescriptionCy(openingTime)).isEqualTo(expectedDescription);
    }
}
