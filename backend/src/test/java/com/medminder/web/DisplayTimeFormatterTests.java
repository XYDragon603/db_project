package com.medminder.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class DisplayTimeFormatterTests {

    @Test
    void formatsTimeAsEnglishOnlyTwentyFourHourClock() {
        assertThat(DisplayTimeFormatter.format(LocalTime.of(15, 0))).isEqualTo("15:00");
        assertThat(DisplayTimeFormatter.format(LocalTime.of(8, 5))).isEqualTo("08:05");
    }
}
