import styles from "../styles/PerformanceInfoPage.module.css";
import PerformanceInfo from "../components/performanceDetail/PerformanceInfo";
import BookingCalendar from "../components/performanceDetail/BookingCalendar";
import PerformanceDetail from "../components/performanceDetail/PerformanceDetail";
import { useEffect, useState } from "react";
import type { PerformanceData } from "../types/PerformanceData";
import { useParams } from "react-router-dom";
import {
  getPerformanceDetail,
  getPerformanceSchedule,
} from "../lib/api/performances";
import type { PerformanceScheduleResponse } from "../types/PerformanceSchedule";

const PerfomanceInfoPage = () => {
  const [performanceData, setPerformanceData] =
    useState<PerformanceData | null>(null);
  const [scheduleData, setScheduleData] =
    useState<PerformanceScheduleResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const { performanceId } = useParams();

  useEffect(() => {
    if (!performanceId) {
      setIsLoading(false);
      return;
    }

    const fetchInfo = async () => {
      try {
        setIsLoading(true);
        const numericPerformanceId = parseInt(performanceId, 10);
        const [detailResponse, scheduleResponse] = await Promise.all([
          getPerformanceDetail(numericPerformanceId),
          getPerformanceSchedule(numericPerformanceId),
        ]);
        setPerformanceData(detailResponse);
        setScheduleData(scheduleResponse);
      } catch (error) {
        console.error("Failed to fetch performance info:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchInfo();
  }, [performanceId]);

  if (isLoading) {
    return <div>정보를 불러오는 중...</div>;
  }

  if (!performanceData || !scheduleData) {
    return <div>공연 정보를 불러오지 못했습니다.</div>;
  }

  return (
    <div className={styles.container}>
      <div className={styles.leftColumn}>
        <PerformanceInfo size="large" performanceData={performanceData} />
        <div className={styles.detailSection}>
          <PerformanceDetail performanceData={performanceData} />
        </div>
      </div>
      <div className={styles.rightColumn}>
        <div className={styles.stickyWrapper}>
          <BookingCalendar
            scheduleData={scheduleData}
            performanceId={parseInt(performanceId!, 10)}
          />
        </div>
      </div>
    </div>
  );
};

export default PerfomanceInfoPage;
