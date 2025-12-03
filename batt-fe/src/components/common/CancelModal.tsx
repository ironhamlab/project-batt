import React, { useState } from "react";
import styles from "./Modal.module.css";
import Modal from "./Modal";
import Swal from "sweetalert2";

interface CancelModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (cancelReason: string) => void;
  bookingInfo: {
    title: string;
    performanceDate: string;
    performanceTime: string;
    totalAmount: number;
    seatCount: number;
  };
  bookingCreatedAt?: number;
  isLoading?: boolean;
}

const CancelModal: React.FC<CancelModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  bookingInfo,
  bookingCreatedAt,
  isLoading = false,
}) => {
  const [cancelReason, setCancelReason] = useState("");
  const [showFeeInfo, setShowFeeInfo] = useState(false);

  const getDaysUntilPerformance = () => {
    const today = new Date();
    const performance = new Date(bookingInfo.performanceDate);
    const diffTime = performance.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const isBookingToday = () => {
    if (!bookingCreatedAt) return false;

    const today = new Date();
    let bookingDate;

    // bookingCreatedAt이 초 단위인지 밀리초 단위인지 확인
    if (bookingCreatedAt < 10000000000) {
      // 초 단위라면 밀리초로 변환
      bookingDate = new Date(bookingCreatedAt * 1000);
    } else {
      // 이미 밀리초 단위
      bookingDate = new Date(bookingCreatedAt);
    }

    // 날짜만 비교 (시간 제외)
    const todayStr =
      today.getFullYear() +
      "-" +
      String(today.getMonth() + 1).padStart(2, "0") +
      "-" +
      String(today.getDate()).padStart(2, "0");
    const bookingStr =
      bookingDate.getFullYear() +
      "-" +
      String(bookingDate.getMonth() + 1).padStart(2, "0") +
      "-" +
      String(bookingDate.getDate()).padStart(2, "0");

    return todayStr === bookingStr;
  };

  const getCancellationFee = () => {
    if (isBookingToday()) return 0;

    const daysUntil = getDaysUntilPerformance();
    if (daysUntil >= 8) return 0;
    if (daysUntil >= 6) return 10;
    if (daysUntil >= 4) return 20;
    if (daysUntil >= 1) return 30;
    return -1;
  };

  const feePercentage = getCancellationFee();
  const feeAmount =
    feePercentage > 0
      ? Math.floor(bookingInfo.totalAmount * (feePercentage / 100))
      : 0;
  const refundAmount = bookingInfo.totalAmount - feeAmount;
  const daysUntil = getDaysUntilPerformance();
  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <div className={styles.modalContent}>
        <h2 className={styles.modalTitle}>예매 취소</h2>

        <div className={styles.bookingInfo}>
          <h3>티켓 정보</h3>
          <div className={styles.infoGrid}>
            <span>공연명</span>
            <span>{bookingInfo.title}</span>
            <span>공연일시</span>
            <span>
              {bookingInfo.performanceDate} {bookingInfo.performanceTime}
            </span>
            <span>매수</span>
            <span>{bookingInfo.seatCount}매</span>
            <span>결제금액</span>
            <span>{bookingInfo.totalAmount.toLocaleString()}원</span>
          </div>
        </div>

        <div className={styles.warningSection}>

          {feePercentage === -1 ? (
            <div className={styles.currentFee} style={{ color: "red" }}>
              공연 당일로 취소가 불가합니다.
            </div>
          ) : (
            <div className={styles.currentFee}>
              <div className={styles.currentFeeRow}>
                {isBookingToday() ? (
                  <p>현재 취소 수수료: <strong>0%</strong></p>
                ) : (
                  <p>현재 취소 수수료: <strong>{feePercentage}%</strong> (공연까지 {daysUntil}일)</p>
                )}
                <div className={styles.tooltipWrapper}>
                  <button
                    type="button"
                    aria-expanded={showFeeInfo}
                    aria-label="수수료 안내 열기/닫기"
                    className={styles.infoButton}
                    onClick={() => setShowFeeInfo((prev) => !prev)}
                  >
                    i
                  </button>
                  {showFeeInfo && (
                    <div className={styles.tooltipBubble} role="dialog">
                      <div className={styles.tooltipTitle}>취소 수수료 안내</div>
                      <div className={styles.tooltipBody}>
                        <div className={styles.feeInfo}>
                          <p>
                            • 예매 당일 및 공연 8일 전까지: <strong>0%</strong> 수수료
                          </p>
                          <p>
                            • 공연 7-6일 전: <strong>10%</strong> 수수료
                          </p>
                          <p>
                            • 공연 5-4일 전: <strong>20%</strong> 수수료
                          </p>
                          <p>
                            • 공연 3-1일 전: <strong>30%</strong> 수수료
                          </p>
                          <p>
                            • 공연 당일: <strong>취소 불가</strong>
                          </p>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              </div>
              <p>수수료: {feeAmount.toLocaleString()}원</p>
              <p>
                <strong>
                  환불 예정 금액: {refundAmount.toLocaleString()}원
                </strong>
              </p>
            </div>
          )}

          <ul className={styles.warningList}>
            <li>취소된 예매는 복구할 수 없습니다.</li>
            <li>환불은 영업일 기준 3-5일 소요됩니다.</li>
          </ul>
        </div>

        <div className={styles.reasonSection}>
          <h3>취소 사유</h3>
          <textarea
            className={styles.reasonTextarea}
            value={cancelReason}
            onChange={(e) => setCancelReason(e.target.value)}
            placeholder="취소 사유를 입력해주세요"
            rows={3}
            maxLength={100}
          />
          <div className={styles.charCount}>{cancelReason.length} / 100</div>
        </div>

        <div className={styles.confirmSection}>
          <p className={styles.confirmText}>정말로 예매를 취소하시겠습니까?</p>
        </div>

        <div className={styles.modalActions}>
          <button
            onClick={onClose}
            className={styles.cancelButton}
            disabled={isLoading}
          >
            아니요
          </button>
          <button
            onClick={() => {
              if (feePercentage === -1) {
                Swal.fire({
                  icon: "error",
                  text: "공연 당일에는 취소가 불가합니다.",
                });
                return;
              }
              if (!cancelReason.trim()) {
                Swal.fire({
                  icon: "error",
                  text: "취소 사유를 입력해주세요.",
                });
                return;
              }
              onConfirm(cancelReason);
            }}
            className={styles.confirmButton}
            disabled={isLoading || feePercentage === -1}
          >
            {isLoading ? "처리 중..." : "네, 취소합니다"}
          </button>
        </div>
      </div>
    </Modal>
  );
};

export default CancelModal;
