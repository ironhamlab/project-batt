package ssafy.batt.common.snowflake;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.ThreadLocalRandom.current;
import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PRIVATE)
public class Snowflake {

  private static final int UNUSED_BITS = 11;
  private static final int EPOCH_BITS = 41;
  private static final int NODE_ID_BITS = 4;
  private static final int SEQUENCE_BITS = 8;

  private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
  private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

  private static final long NODE_ID = current().nextLong(MAX_NODE_ID + 1);
  private static final long START_TIME_MILLIS = 1704067200000L; // 2024-01-01T00:00:00Z
  private static long lastTimeMillis = START_TIME_MILLIS;
  private static long sequence = 0L;

  public static synchronized long nextId() {
    long currentTimeMillis = currentTimeMillis();

    if (currentTimeMillis < lastTimeMillis) {
      throw new IllegalStateException("Invalid Time");
    }

    if (currentTimeMillis == lastTimeMillis) {
      sequence = (sequence + 1) & MAX_SEQUENCE;
      if (sequence == 0) {
        currentTimeMillis = waitNextMillis(currentTimeMillis);
      }
    } else {
      sequence = 0;
    }

    lastTimeMillis = currentTimeMillis;
    long timestamp = currentTimeMillis - START_TIME_MILLIS;

    return (timestamp << (NODE_ID_BITS + SEQUENCE_BITS))
           | (NODE_ID << SEQUENCE_BITS)
           | sequence;
  }

  private static long waitNextMillis(long currentTimestamp) {
    while (currentTimestamp <= lastTimeMillis) {
      currentTimestamp = currentTimeMillis();
    }
    return currentTimestamp;
  }

  public static String formattingReservationId() {
    String id = String.format("%016d", nextId());
    return id.replaceAll("(\\d{4})(?=\\d)", "$1-");
  }
}