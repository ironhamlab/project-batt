package ssafy.batt.common.date;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDate;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PRIVATE)
public class DateUtil {

  public static int calculateDaysBetween(LocalDate today, LocalDate performanceDate) {
    return (int) (performanceDate.toEpochDay() - today.toEpochDay());
  }
}