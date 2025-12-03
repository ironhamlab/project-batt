import React from "react";
import styles from "@styles/PerformanceInfo.module.css";
import type { PerformanceData } from "../../types/PerformanceData";

interface PerformanceInfoProps {
  size?: "large" | "small";
  performanceData: PerformanceData | null;
}

const PerformanceInfo: React.FC<PerformanceInfoProps> = ({
  size = "small",
  performanceData,
}) => {
  const cardClasses = `${styles.card} ${
    size === "large" ? styles.cardLarge : styles.cardSmall
  }`;

  if (!performanceData) {
    return <div>공연 정보를 불러오지 못했습니다.</div>;
  }

  return (
    <div>
      <div className={styles.title}>{performanceData.title}</div>
      <div className={cardClasses}>
        <img
          src={performanceData.posterImageUrl}
          alt={`${performanceData.title} Poster`}
          className={styles.posterImage}
        />
        <div className={styles.info}>
          <div className={styles.details}>
            <span className={styles.label}>장소</span>
            <span>{performanceData.venueName}</span>
            <span className={styles.label}>공연기간</span>
            <span>
              {performanceData.performanceStartDate} ~{" "}
              {performanceData.performanceEndDate}
            </span>
            <span className={styles.label}>공연시간</span>
            <span>{performanceData.durationMinute}분</span>
            <span className={styles.label}>관람연령</span>
            <span>
              {performanceData.ageRestriction === 0
                ? "전체 관람가"
                : `${performanceData.ageRestriction}세 이상 관람가`}
            </span>
            <span className={styles.label}>가격</span>
            <div className={styles.priceList}>
              {performanceData.prices.VIP && (
                <span>
                  VIP석 {performanceData.prices.VIP.toLocaleString()}원
                </span>
              )}
              {performanceData.prices.R && (
                <span>R석 {performanceData.prices.R.toLocaleString()}원</span>
              )}
              {performanceData.prices.S && (
                <span>S석 {performanceData.prices.S.toLocaleString()}원</span>
              )}
              {performanceData.prices.A && (
                <span>A석 {performanceData.prices.A.toLocaleString()}원</span>
              )}
              {performanceData.prices.B && (
                <span>B석 {performanceData.prices.B.toLocaleString()}원</span>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PerformanceInfo;
