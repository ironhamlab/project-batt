package ssafy.batt.common.date;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DateUtilTest {

  private static Stream<Arguments> provideDatesForDaysBetween() {
    return Stream.of(
        Arguments.of("2025-10-01", "2025-10-01", 0),
        Arguments.of("2025-10-01", "2025-10-02", 1),
        Arguments.of("2025-10-01", "2025-10-07", 6),
        Arguments.of("2025-10-01", "2025-10-10", 9)
    );
  }

  @ParameterizedTest
  @MethodSource("provideDatesForDaysBetween")
  @DisplayName("두 날짜 사이의 일수를 계산할 수 있다.")
  void calculateDaysBetweenTest(LocalDate today, LocalDate performanceDate, int days) {

    // given & when
    int result = DateUtil.calculateDaysBetween(today, performanceDate);

    // then
    assertEquals(days, result);
  }
}