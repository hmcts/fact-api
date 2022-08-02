package uk.gov.hmcts.dts.fact.util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static uk.gov.hmcts.dts.fact.util.Action.*;

public class ActionTest {
    @Test
    void testFindByName() {
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(findByName("nearest")).isEqualTo(NEAREST);
        softly.assertThat(findByName("documents")).isEqualTo(DOCUMENTS);
        softly.assertThat(findByName("update")).isEqualTo(UPDATE);
        softly.assertThat(findByName("undefined")).isEqualTo(UNDEFINED);
        softly.assertThatThrownBy(() -> findByName("unknown action"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown action: unknown action");
        softly.assertAll();
    }
}
