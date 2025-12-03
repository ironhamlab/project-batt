import React, { useState, useEffect, useMemo } from "react";
import styles from "@styles/TransferModal.module.css";
import type { Transfer } from "../../types/Transfer";
import { createBid } from "../../lib/api/transfers";
import Swal from "sweetalert2";

interface TransferModalProps {
  isOpen: boolean;
  onClose: () => void;
  transfer: Transfer;
  userCoins: number;
}

const TransferModal: React.FC<TransferModalProps> = ({
  isOpen,
  onClose,
  transfer,
  userCoins,
}) => {
  const [step, setStep] = useState(1);
  const [transferAmount, setTransferAmount] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const formatDate = (dateString?: string) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const weekday = ["일", "월", "화", "수", "목", "금", "토"][date.getDay()];
    return `${year}년 ${month}월 ${day}일 ${weekday}요일`;
  };

  const seatLabel = useMemo(() => {
    const seatArray = Array.isArray(transfer.seatNumber) 
      ? transfer.seatNumber.filter(seat => seat && seat.trim() !== "")
      : transfer.seatNumber 
        ? [String(transfer.seatNumber)]
        : [];
    
    if (seatArray.length === 0) {
      return "좌석 정보 없음";
    }
    
    return seatArray.join(", ");
  }, [transfer.seatNumber]);

  useEffect(() => {
    if (isOpen) {
      setStep(1);
      setTransferAmount("");
      setIsSubmitting(false);
    }
  }, [isOpen]);

  if (!isOpen) {
    return null;
  }

  const handleTransferAmountChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const value = e.target.value.replace(/[^0-9]/g, "");
    setTransferAmount(value);
  };

  const handleNextStep = () => {
    const transferValue = parseInt(transferAmount, 10);
    if (isNaN(transferValue) || transferValue <= transfer.currentHighestBid) {
      Swal.fire({
        icon: "error",
        text: "현재 최고 BATT보다 높은 금액을 입력해야 합니다.",
      });
      return;
    }
    if (transferValue > userCoins) {
      Swal.fire({
        icon: "error",
        text: "사용 가능 BATT가 부족합니다.",
      });
      return;
    }
    setStep(2);
  };

  const handlePayment = async () => {
    if (isSubmitting) return;

    setIsSubmitting(true);
    try {
      await createBid(transfer.transferId, {
        coin: parseInt(transferAmount, 10),
      });

      Swal.fire({
        icon: "success",
        text: "입찰이 성공적으로 등록되었습니다!",
      });
      onClose();
    } catch (error: any) {
      console.error("입찰 등록 실패:", error);
      if (error.response?.status === 400) {
        Swal.fire({
          icon: "error",
          text: "입찰 금액이 유효하지 않습니다. 현재 BATT보다 높은 금액을 입력해주세요.",
        });
      } else if (error.response?.status === 401) {
        Swal.fire({
          icon: "error",
          text: "로그인이 필요합니다.",
        });
      } else if (error.response?.status === 403) {
        Swal.fire({
          icon: "error",
          text: "입찰 권한이 없습니다.",
        });
      } else {
        Swal.fire({
          icon: "error",
          text: "입찰 등록 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
        });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <button className={styles.closeButton} onClick={onClose}>
          &times;
        </button>

        {step === 1 && (
          <>
            <div className={styles.modalHeader}>
              <h2 className={styles.modalTitle}>양도 티켓 입찰</h2>

            </div>

            {/* 공연 정보 카드 */}
            <div className={styles.performanceCard}>
              <div className={styles.cardHeader}>
                <h3>공연 정보</h3>
              </div>
              <div className={styles.cardContent}>
                <div className={styles.infoRow}>
                  <span className={styles.label}>공연 일시</span>
                  <span className={styles.value}>
                    {formatDate(transfer.performanceDate)}
                    {transfer.performanceTime ? ` ${transfer.performanceTime}` : ""}
                  </span>
                </div>
                <div className={styles.infoRow}>
                  <span className={styles.label}>티켓 가격</span>
                  <span className={styles.value}>{transfer.price.toLocaleString()}원</span>
                </div>
                <div className={styles.infoRow}>
                  <span className={styles.label}>좌석 정보</span>
                  <span className={styles.value}>
                    {seatLabel}
                  </span>
                </div>
              </div>
            </div>

            {/* 입찰 정보 카드 */}
            <div className={styles.bidCard}>
              <div className={styles.cardHeader}>
                <h3>입찰 정보</h3>
              </div>
              <div className={styles.cardContent}>
                <div className={styles.bidInfo}>
                  <div className={styles.bidRow}>
                    <span className={styles.bidLabel}>현재 최고 BATT</span>
                    <span className={styles.bidValue}>{transfer.currentHighestBid.toLocaleString()} BATT</span>
                  </div>
                  <div className={styles.bidRow}>
                    <span className={styles.bidLabel}>보유 BATT</span>
                    <span className={styles.bidValue}>{userCoins.toLocaleString()} BATT</span>
                  </div>
                </div>
                
                <div className={styles.inputSection}>
                  <label className={styles.inputLabel}>입찰 BATT</label>
                  <input
                    type="text"
                    className={styles.battInput}
                    value={transferAmount}
                    onChange={handleTransferAmountChange}
                    placeholder="BATT 입력"
                  />
                  <p className={styles.inputHint}>
                    현재 최고 BATT보다 높은 금액을 입력해주세요
                  </p>
                </div>
              </div>
            </div>

            <div className={styles.buttonContainer}>
              <button className={styles.submitButton} onClick={handleNextStep}>
                다음 단계
              </button>
            </div>
          </>
        )}

        {step === 2 && (
          <>
            <div className={styles.modalHeader}>
              <h2 className={styles.modalTitle}>입찰 확인</h2>
              <p className={styles.modalSubtitle}>주의사항을 확인하고 입찰을 완료해주세요</p>
            </div>

            {/* 입찰 요약 */}
            <div className={styles.summaryCard}>
              <div className={styles.cardHeader}>
                <h3>입찰 요약</h3>
              </div>
              <div className={styles.cardContent}>
                <div className={styles.summaryRow}>
                  <span className={styles.summaryLabel}>입찰 BATT</span>
                  <span className={styles.summaryValue}>{parseInt(transferAmount, 10).toLocaleString()} BATT</span>
                </div>
                <div className={styles.summaryRow}>
                  <span className={styles.summaryLabel}>공연</span>
                  <span className={styles.summaryValue}>
                    {formatDate(transfer.performanceDate)}
                    {transfer.performanceTime ? ` ${transfer.performanceTime}` : ""}
                  </span>
                </div>
              </div>
            </div>

            {/* 주의사항 */}
            <div className={styles.termsCard}>
              <div className={styles.cardHeader}>
                <h3>주의사항</h3>
              </div>
              <div className={styles.termsContent}>
                <div className={styles.termItem}>
                  <span className={styles.termIcon}>⚠️</span>
                  <p>티켓 양도 및 구매는 신중하게 결정해주세요. 거래가 시작된 이후에는 취소가 불가능합니다.</p>
                </div>
                <div className={styles.termItem}>
                  <span className={styles.termIcon}>🔒</span>
                  <p>개인정보(실명, 연락처 등)는 거래 상대방에게 공개될 수 있습니다.</p>
                </div>
                <div className={styles.termItem}>
                  <span className={styles.termIcon}>📋</span>
                  <p>모든 거래는 본 사이트의 규정을 따르며, 규정 위반 시 서비스 이용에 제한을 받을 수 있습니다.</p>
                </div>
                <div className={styles.termItem}>
                  <span className={styles.termIcon}>💰</span>
                  <p>최종 결제 금액은 제한 시간내에 결제되어야 하며, 티켓은 환불되지 않습니다.</p>
                </div>
              </div>
            </div>

            <div className={styles.buttonContainer}>
              <button
                className={styles.submitButton}
                onClick={handlePayment}
                disabled={isSubmitting}
              >
                {isSubmitting ? "입찰 등록 중..." : "동의 및 입찰하기"}
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default TransferModal;
