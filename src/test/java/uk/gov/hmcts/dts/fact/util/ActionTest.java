package uk.gov.hmcts.dts.fact.util;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static uk.gov.hmcts.dts.fact.util.Action.DOCUMENTS;
import static uk.gov.hmcts.dts.fact.util.Action.NEAREST;
import static uk.gov.hmcts.dts.fact.util.Action.UNDEFINED;
import static uk.gov.hmcts.dts.fact.util.Action.UPDATE;
import static uk.gov.hmcts.dts.fact.util.Action.findAction;
import static uk.gov.hmcts.dts.fact.util.Action.isNearest;

class ActionTest {
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

    @Test
    void testIsNearest() {
        final SoftAssertions softly = new SoftAssertions();
        softly.assertThat(isNearest(NEAREST)).isEqualTo(true);
        softly.assertThat(isNearest(DOCUMENTS)).isEqualTo(false);
        softly.assertThat(isNearest(UPDATE)).isEqualTo(false);
        softly.assertThat(isNearest(UNDEFINED)).isEqualTo(false);
        softly.assertAll();
    }
}
