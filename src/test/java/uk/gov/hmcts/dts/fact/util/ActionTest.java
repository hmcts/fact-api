package uk.gov.hmcts.dts.fact.util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static uk.gov.hmcts.dts.fact.util.Action.*;

public class ActionTest {
    @Test
    void testFindByName() {
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(findAction("nearest")).isEqualTo(NEAREST);
        softly.assertThat(findAction("documents")).isEqualTo(DOCUMENTS);
        softly.assertThat(findAction("update")).isEqualTo(UPDATE);
        softly.assertThat(findAction("undefined")).isEqualTo(UNDEFINED);
        softly.assertThatThrownBy(() -> findAction("unknown action"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown action: unknown action");
        softly.assertAll();
    }
}
