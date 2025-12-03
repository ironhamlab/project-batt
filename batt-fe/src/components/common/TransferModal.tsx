import React, { useMemo, useState } from "react";
import styles from "./Modal.module.css";
import Modal from "./Modal";
import Swal from "sweetalert2";
import dayjs, { Dayjs } from "dayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DateTimePicker } from "@mui/x-date-pickers/DateTimePicker";

interface TransferModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (transferEndDateTime: number) => void;
  performanceDate: string;
  bookingCreatedAt?: number;
  isLoading?: boolean;
}

const TransferModal: React.FC<TransferModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  performanceDate,
  bookingCreatedAt,
  isLoading = false,
}) => {
  const [selectedDateTime, setSelectedDateTime] = useState<Dayjs | null>(null);
  const [showFeeInfo, setShowFeeInfo] = useState(false);

  const getDaysUntilPerformance = () => {
    const today = new Date();
    const performance = new Date(performanceDate);
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

  const getFeePercentage = () => {
    if (isBookingToday()) return 0; // 예매 당일은 무조건 0%

    const daysUntil = getDaysUntilPerformance();
    if (daysUntil >= 8) return 0;
    if (daysUntil >= 6) return 10;
    if (daysUntil >= 4) return 20;
    if (daysUntil >= 1) return 30;
    return -1;
  };

  const getSelectedDateFeePercentage = () => {
    if (!selectedDateTime) return null;

    const performance = dayjs(performanceDate);
    const diffDays = Math.ceil(
      (performance.startOf("day").valueOf() - selectedDateTime.valueOf()) /
        (1000 * 60 * 60 * 24),
    );

    if (diffDays >= 8) return 0;
    if (diffDays >= 6) return 10;
    if (diffDays >= 4) return 20;
    if (diffDays >= 1) return 30;
    return -1;
  };

  const minDateTime = useMemo(() => dayjs().add(1, "minute").second(0).millisecond(0), []);
  const maxDateTime = useMemo(() =>
    dayjs(performanceDate).startOf("day").subtract(1, "day").endOf("day"),
  [performanceDate]);

  const handleSubmit = () => {
    if (!selectedDateTime) {
      Swal.fire({
        icon: "error",
        text: "양도 마감 일시를 선택해주세요.",
      });
      return;
    }

    const isBeforeMin = selectedDateTime.isBefore(minDateTime);
    const isAfterMax = selectedDateTime.isAfter(maxDateTime);
    
    if (isBeforeMin) {
      Swal.fire({
        icon: "error",
        text: "이미 지난 시간입니다!",
      });
      return;
    }
    
    if (isAfterMax) {
      Swal.fire({
        icon: "error",
        text: "선택 가능한 범위를 벗어났습니다.",
      });
      return;
    }

    const timestamp = selectedDateTime.unix(); // 초 단위 타임스탬프
    onConfirm(timestamp);
  };

  const feePercentage = getFeePercentage();
  const daysUntil = getDaysUntilPerformance();

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <div className={styles.modalContent}>
        <h2 className={styles.modalTitle}>티켓 양도 신청</h2>

        <div className={styles.infoSection}>
          <div className={styles.sectionHeader}></div>
          <div className={styles.currentFee}>
            <div className={styles.currentFeeRow}>
              {feePercentage === -1 ? (
                <span style={{ color: "red" }}>현재 양도 불가 (공연 당일)</span>
              ) : isBookingToday() ? (
                <span>현재 수수료: <strong>0%</strong></span>
              ) : (
                <>
                  현재 수수료: <strong>{feePercentage}%</strong> (공연까지 {daysUntil}일)
                </>
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
                    <div className={styles.tooltipTitle}>수수료 안내</div>
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
                          • 공연 당일: <strong>양도 불가</strong>
                        </p>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
          {selectedDateTime && (
            <div className={styles.selectedDateFee}>
              {(() => {
                const selectedFee = getSelectedDateFeePercentage();
                const performance = dayjs(performanceDate);
                const diffDays = Math.ceil(
                  (performance.startOf("day").valueOf() - selectedDateTime.valueOf()) /
                    (1000 * 60 * 60 * 24),
                );

                return selectedFee === -1 ? (
                  <span style={{ color: "red" }}>
                    ⚠️ 선택한 마감일({selectedDateTime.format("YYYY-MM-DD HH:mm")})은
                    양도 불가 시점입니다
                  </span>
                ) : (
                  <span style={{ color: "#2196F3", fontWeight: "bold" }}>
                    📅 선택한 마감일 기준 수수료: {selectedFee}% (공연까지 {diffDays}일)
                  </span>
                );
              })()}
            </div>
          )}
        </div>

        <div className={styles.warningSection}>
          <p className={styles.warningText}>
            ⚠️{" "}
            <strong>
              양도받을 사람이 없을 시에는 양도 마감일 기준으로 수수료 제외 후
              환불됩니다.
            </strong>
          </p>
        </div>

        <div className={styles.inputSection}>
          <h3>양도 마감 시간 설정</h3>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DateTimePicker
              value={selectedDateTime}
              onChange={(newValue) => setSelectedDateTime(newValue)}
              minDateTime={minDateTime}
              maxDateTime={maxDateTime}
              slotProps={{
                textField: {
                  fullWidth: true,
                },
              }}
            />
          </LocalizationProvider>
        </div>

        <div className={styles.modalActions}>
          <button
            onClick={onClose}
            className={styles.cancelButton}
            disabled={isLoading}
          >
            취소
          </button>
          <button
            onClick={handleSubmit}
            className={styles.confirmButton}
            disabled={
              isLoading ||
              feePercentage === -1 ||
              (!!selectedDateTime && getSelectedDateFeePercentage() === -1)
            }
          >
            {isLoading ? "처리 중..." : "양도 신청"}
          </button>
        </div>
      </div>
    </Modal>
  );
};

export default TransferModal;
