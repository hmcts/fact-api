package uk.gov.hmcts.dts.fact.data.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.junit.After;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import uk.gov.hmcts.dts.fact.exception.PendingMigrationScriptException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.reset;

@ExtendWith(MockitoExtension.class)
public class FlywayNoOpStrategyTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Flyway flyway;

    @Mock
    private MigrationInfoService infoService;

    @Mock
    private MigrationInfo info;

    private final FlywayMigrationStrategy strategy = new FlywayNoOpStrategy();

    @After
    public void tearUp() {
        reset(flyway, infoService, info);
    }

    @Test
    public void should_not_throw_exception_when_all_migrations_are_applied() {
        MigrationInfo[] infos = {info, info};
        given(flyway.info()).willReturn(infoService);
        given(infoService.all()).willReturn(infos);
        given(info.getState()).willReturn(MigrationState.SUCCESS);

        Throwable exception = catchThrowable(() -> strategy.migrate(flyway));
        assertThat(exception).isNull();
    }

    @Test
    public void should_throw_exception_when_one_migration_is_pending() {
        MigrationInfo[] infos = {info, info};
        given(flyway.info()).willReturn(infoService);
        given(infoService.all()).willReturn(infos);
        given(info.getState()).willReturn(MigrationState.SUCCESS, MigrationState.PENDING);

        Throwable exception = catchThrowable(() -> strategy.migrate(flyway));
        assertThat(exception)
            .isInstanceOf(PendingMigrationScriptException.class)
            .hasMessageStartingWith("Found migration not yet applied");
    }
}

