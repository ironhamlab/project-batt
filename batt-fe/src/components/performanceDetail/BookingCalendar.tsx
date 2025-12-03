import { useState, useEffect, useMemo } from "react";
import styles from "@styles/BookingCalendar.module.css";
import type {
  PerformanceScheduleResponse,
  PerformanceTimeInfo,
} from "../../types/PerformanceSchedule";
import { useNavigate } from "react-router-dom";

const toLocalDate = (dateString: string): Date => {
  const [year, month, day] = dateString.split("-").map(Number);
  return new Date(year, month - 1, day);
};

const toDateKey = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const getBookingCutoff = (performanceDate: Date): Date => {
  return new Date(
    performanceDate.getFullYear(),
    performanceDate.getMonth(),
    performanceDate.getDate() - 1,
    17,
    0,
    0,
    0,
  );
};

const isDateSelectableByCutoff = (
  performanceDate: Date,
  now: Date,
): boolean => {
  return now < getBookingCutoff(performanceDate);
};

interface BookingCalendarProps {
  scheduleData: PerformanceScheduleResponse;
  performanceId: number;
}

const BookingCalendar = ({
  scheduleData,
  performanceId,
}: BookingCalendarProps) => {
  const navigate = useNavigate();
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [availableSessions, setAvailableSessions] = useState<
    PerformanceTimeInfo[]
  >([]);
  const [selectedSession, setSelectedSession] =
    useState<PerformanceTimeInfo | null>(null);

  const performanceScheduleMap = useMemo(() => {
    const map = new Map<string, PerformanceTimeInfo[]>();
    scheduleData.performanceSchedule.forEach((item) => {
      map.set(item.performanceDate, item.performanceTimeInfo);
    });
    return map;
  }, [scheduleData.performanceSchedule]);

  const performanceStartDate = useMemo(
    () => toLocalDate(scheduleData.performanceSchedule[0].performanceDate),
    [scheduleData.performanceSchedule],
  );

  const performanceEndDate = useMemo(
    () =>
      toLocalDate(
        scheduleData.performanceSchedule[
          scheduleData.performanceSchedule.length - 1
        ].performanceDate,
      ),
    [scheduleData.performanceSchedule],
  );

  // 공연 종료 여부: 오늘(자정) 기준으로 종료일이 그 이전(어제까지)이라면 마감 처리
  const isPerformanceEnded = useMemo(() => {
    const now = new Date();
    const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const endDateStart = new Date(
      performanceEndDate.getFullYear(),
      performanceEndDate.getMonth(),
      performanceEndDate.getDate(),
      0,
      0,
      0,
      0,
    );
    return endDateStart < todayStart;
  }, [performanceEndDate]);

  useEffect(() => {
    setCurrentDate(
      new Date(
        performanceStartDate.getFullYear(),
        performanceStartDate.getMonth(),
        1,
      ),
    );

    const now = new Date();
    for (const schedule of scheduleData.performanceSchedule) {
      const date = toLocalDate(schedule.performanceDate);
      if (isDateSelectableByCutoff(date, now)) {
        setSelectedDate(date);
        break;
      }
    }
  }, [scheduleData, performanceStartDate]);

  useEffect(() => {
    if (selectedDate) {
      const dateKey = toDateKey(selectedDate);
      const sessions = performanceScheduleMap.get(dateKey) || [];
      setAvailableSessions(sessions);
      setSelectedSession(sessions.length > 0 ? sessions[0] : null);
    } else {
      setAvailableSessions([]);
      setSelectedSession(null);
    }
  }, [selectedDate, performanceScheduleMap]);

  const handleDateClick = (day: number) => {
    const clickedDate = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth(),
      day,
    );
    const dateKey = toDateKey(clickedDate);
    if (performanceScheduleMap.has(dateKey)) {
      setSelectedDate(clickedDate);
    }
  };

  const handlePrevMonth = () => {
    setCurrentDate(
      new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1),
    );
  };
  const handleNextMonth = () => {
    setCurrentDate(
      new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1),
    );
  };

  const isPrevMonthDisabled =
    currentDate.getFullYear() === performanceStartDate.getFullYear() &&
    currentDate.getMonth() === performanceStartDate.getMonth();
  const isNextMonthDisabled =
    currentDate.getFullYear() === performanceEndDate.getFullYear() &&
    currentDate.getMonth() === performanceEndDate.getMonth();

  const renderDates = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const startOffset = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1;
    const dates = [];
    const now = new Date();

    for (let i = 0; i < startOffset; i++) {
      dates.push(<div key={`empty-${i}`} className={styles.dateCell}></div>);
    }

    for (let i = 1; i <= daysInMonth; i++) {
      const date = new Date(year, month, i);
      const dateKey = toDateKey(date);
      const hasPerformance = performanceScheduleMap.has(dateKey);
      const isSelectable =
        hasPerformance && isDateSelectableByCutoff(date, now);
      const isSelected = selectedDate?.getTime() === date.getTime();

      const cellClasses = [
        styles.dateCell,
        isSelectable ? styles.selectable : "",
        !isSelectable ? styles.disabled : "",
        isSelected ? styles.selected : "",
      ].join(" ");

      dates.push(
        <div
          key={i}
          className={cellClasses}
          onClick={() => isSelectable && handleDateClick(i)}
        >
          {i}
        </div>,
      );
    }
    return dates;
  };

  if (isPerformanceEnded) {
    return (
      <div className={styles.container}>
        <div className={styles.section}>
          <p className={styles.noSessionText}>마감된 공연입니다.</p>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.section}>
        <h2 className={styles.title}>날짜 선택</h2>
        <div className={styles.calendar}>
          <div className={styles.monthHeader}>
            <button
              onClick={handlePrevMonth}
              className={styles.navButton}
              disabled={isPrevMonthDisabled}
            >
              ‹
            </button>
            <span>
              {currentDate.getFullYear()}년 {currentDate.getMonth() + 1}월
            </span>
            <button
              onClick={handleNextMonth}
              className={styles.navButton}
              disabled={isNextMonthDisabled}
            >
              ›
            </button>
          </div>
          <div className={styles.weekdays}>
            {["월", "화", "수", "목", "금", "토", "일"].map((day) => (
              <div key={day}>{day}</div>
            ))}
          </div>
          <div className={styles.datesGrid}>{renderDates()}</div>
        </div>
      </div>

      <hr className={styles.separator} />

      {selectedDate ? (
        <>
          <div className={styles.section}>
            <h2 className={styles.title}>회차 선택</h2>
            <div className={styles.sessionButtons}>
              {availableSessions.length > 0 ? (
                availableSessions.map((session) => (
                  <button
                    key={session.performanceScheduleId}
                    className={`${styles.timeButton} ${
                      selectedSession?.performanceScheduleId ===
                      session.performanceScheduleId
                        ? styles.selected
                        : ""
                    }`}
                    onClick={() => setSelectedSession(session)}
                  >
                    {session.performanceTime}
                  </button>
                ))
              ) : (
                <p className={styles.noSessionText}>
                  선택한 날짜에 공연 회차가 없습니다.
                </p>
              )}
            </div>
          </div>

          <div className={styles.section}>
            <div
              className={styles.reservationButton}
              onClick={() => {
                if (selectedDate && selectedSession) {
                  sessionStorage.setItem(
                    "performanceId",
                    String(performanceId),
                  );
                  sessionStorage.setItem(
                    "scheduleId",
                    String(selectedSession.performanceScheduleId),
                  );
                  const localYmd = new Date(
                    selectedDate.getTime() -
                      selectedDate.getTimezoneOffset() * 60000,
                  )
                    .toISOString()
                    .slice(0, 10);
                  sessionStorage.setItem("performanceDate", localYmd);
                  sessionStorage.setItem(
                    "performanceTime",
                    String(selectedSession.performanceTime),
                  );
                  navigate("/booking");
                } else {
                  console.log("날짜와 회차를 모두 선택해주세요.");
                }
              }}
            >
              예매하기
            </div>
          </div>
        </>
      ) : (
        <div className={styles.section}>
          <h2 className={styles.title}>회차 선택</h2>
          <p className={styles.noSessionText}>
            달력에서 관람을 원하시는 날짜를 선택해주세요.
          </p>
        </div>
      )}
    </div>
  );
};

export default BookingCalendar;
