import { useEffect, useState } from "react";
import TransferTicketList from "../components/transfer/TransferTicketList";
import styles from "../styles/TransferPage.module.css";
import PerformanceInfo from "../components/performanceDetail/PerformanceInfo";
import { useParams } from "react-router-dom";
import { getPerformanceDetail } from "../lib/api/performances";
import type { PerformanceData } from "../types/PerformanceData";

const TransferPage = () => {
  const { performanceId } = useParams();
  const [performanceData, setPerformanceData] =
    useState<PerformanceData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchDetail = async () => {
      if (!performanceId) {
        setPerformanceData(null);
        setIsLoading(false);
        return;
      }
      try {
        setIsLoading(true);
        const data = await getPerformanceDetail(Number(performanceId));
        setPerformanceData(data);
      } catch (e) {
        console.error("공연 정보 로딩 실패:", e);
        setPerformanceData(null);
      } finally {
        setIsLoading(false);
      }
    };
    fetchDetail();
  }, [performanceId]);

  return (
    <div className={styles.pageContainer}>
      {/* 공연 정보 */}
      <div className={styles.performanceSection}>
        {isLoading ? (
          <div className={styles.loadingContainer}>
            로딩 중...
          </div>
        ) : performanceData ? (
          <PerformanceInfo size="small" performanceData={performanceData} />
        ) : (
          <div className={styles.errorContainer}>
            공연 정보를 불러올 수 없습니다.
          </div>
        )}
      </div>

      {/* 양도 티켓 리스트 */}
      <div className={styles.transferSection}>
        <TransferTicketList />
      </div>
    </div>
  );
};

export default TransferPage;
