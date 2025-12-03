import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/TransferList.module.css";
import TransferModal from "./TransferModal";
import type { Transfer } from "../../types/Transfer";
import { useParams, useSearchParams } from "react-router-dom";
import {
  fetchTransfersByPerformance,
  openTransfersSSE,
} from "../../lib/api/transfers";
import { useAuthStore } from "../../stores/authStore";
import Swal from "sweetalert2";

const TransferItem: React.FC<{
  transfer: Transfer;
  onTransferClick: (transfer: Transfer) => void;
  isNew: boolean;
}> = ({ transfer, onTransferClick, isNew }) => {
  const [remainingLabel, setRemainingLabel] = useState<string>("");
  const isClosed = transfer.status === "ENDED";
  const isEnded = remainingLabel === "마감됨";

  useEffect(() => {
    if (isClosed || transfer.transferEndDateTime == null) {
      setRemainingLabel(transfer.status === "ENDED" ? "마감됨" : "진행중");
      return;
    }

    const endMsUtc = Math.floor(Number(transfer.transferEndDateTime) * 1000);

    const update = () => {
      const nowMsUtc = new Date().getTime();
      const diff = endMsUtc - nowMsUtc;
      if (diff <= 0) {
        setRemainingLabel("마감됨");
        return;
      }
      const hundredHoursMs = 100 * 60 * 60 * 1000;
      if (diff > hundredHoursMs) {
        const days = Math.floor(diff / (24 * 60 * 60 * 1000));
        setRemainingLabel(`D-${days}`);
      } else {
        const hours = Math.floor(diff / (60 * 60 * 1000));
        const minutes = Math.floor((diff % (60 * 60 * 1000)) / (60 * 1000));
        const seconds = Math.floor((diff % (60 * 1000)) / 1000);
        const hh = String(hours).padStart(2, "0");
        const mm = String(minutes).padStart(2, "0");
        const ss = String(seconds).padStart(2, "0");
        setRemainingLabel(`${hh}:${mm}:${ss}`);
      }
    };

    update();
    const intervalId = window.setInterval(update, 1000);
    return () => window.clearInterval(intervalId);
  }, [isClosed, transfer.transferEndDateTime, transfer.status]);

  const formatDate = (dateString?: string) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const weekday = ["일", "월", "화", "수", "목", "금", "토"][date.getDay()];
    return `${year}/${month}/${day} ${weekday}요일`;
  };

  const ticketDetails = useMemo(() => {
    const datePart = formatDate(transfer.performanceDate);
    const timePart = transfer.performanceTime ? ` ${transfer.performanceTime}` : "";
    
    const seatArray = Array.isArray(transfer.seatNumber) 
      ? transfer.seatNumber.filter(seat => seat && seat.trim() !== "")
      : transfer.seatNumber 
        ? [String(transfer.seatNumber)]
        : [];
    
    let displaySeats = "";
    if (seatArray.length === 0) {
      displaySeats = "좌석 정보 없음";
    } else if (seatArray.length === 1) {
      displaySeats = seatArray[0];
    } else if (seatArray.length <= 3) {
      displaySeats = seatArray.join(", ");
    } else {
      displaySeats = `${seatArray.slice(0, 2).join(", ")} 외 ${seatArray.length - 2}개`;
    }
    
    return {
      dateTime: `${datePart}${timePart}`,
      seats: displaySeats,
      allSeats: seatArray
    };
  }, [transfer.performanceDate, transfer.performanceTime, transfer.seatNumber]);

  const itemClassName = `${styles.ticketItem} ${isClosed ? styles.closed : ""}`;

  return (
    <div className={itemClassName}>
      <div className={styles.leftSection}>
        <div className={styles.deadline}>
          {isClosed ? (
            transfer.status === "ENDED" ? (
              "마감"
            ) : (
              "진행중"
            )
          ) : (
            <>
              <div>
                {isNew ? (
                  <span style={{ color: "#ff3b30", fontWeight: 800 }}>NEW</span>
                ) : (
                  "남은시간"
                )}
              </div>
              <div>
                <span className={styles.timer}>{remainingLabel}</span>
              </div>
            </>
          )}
        </div>
      </div>
      <div className={styles.centerSection}>
        <div className={styles.performanceInfo}>
          <div className={styles.dateTime}>{ticketDetails.dateTime}</div>
          <div className={styles.seats}>{ticketDetails.seats}</div>
        </div>
        <div className={styles.price}>
          {transfer.price.toLocaleString()}원
        </div>
      </div>
      <div className={styles.rightSection}>
        <span className={styles.participants}>
          {isClosed ? "최종" : "현재"}:{" "}
          {transfer.currentHighestBid.toLocaleString()} BATT
        </span>
        <button
          className={styles.actionButton}
          disabled={isClosed || isEnded}
          onClick={() => onTransferClick(transfer)}
        >
          참여하기 &gt;
        </button>
      </div>
    </div>
  );
};

const TransferTicketList: React.FC = () => {
  const params = useParams();
  const [searchParams] = useSearchParams();
  const performanceId =
    params.performanceId || searchParams.get("performanceId") || undefined;

  const [transfers, setTransfers] = useState<Transfer[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadedOnce, setIsLoadedOnce] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedTransfer, setSelectedTransfer] = useState<Transfer | null>(
    null,
  );
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const eventSourceRef = useRef<EventSource | null>(null);
  const userCoins = useAuthStore((s) => s.coin);
  const memberId = useAuthStore((s) => s.memberId);
  const [newIds, setNewIds] = useState<Set<number>>(new Set());
  const [showTooltip, setShowTooltip] = useState(false);

  useEffect(() => {
    const fetchTransfers = async () => {
      if (!performanceId) {
        setTransfers([]);
        setIsLoading(false);
        setIsLoadedOnce(false);
        return;
      }
      setIsLoading(true);
      try {
        const data = await fetchTransfersByPerformance(performanceId, page, 20);
        setTransfers(data.transfers);
        setTotalPages(data.pageInfo?.totalPages ?? 0);
        setIsLoadedOnce(true);
      } catch (error) {
        console.error("양도 목록 로딩 실패:", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchTransfers();
  }, [performanceId, page]);

  useEffect(() => {
    if (!performanceId || !isLoadedOnce) return;

    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }

    const es = openTransfersSSE(performanceId, {
      onUpdate: (updated) => {
        setTransfers((prev) => {
          const idx = prev.findIndex(
            (t) => t.transferId === updated.transferId,
          );
          if (idx === -1) {
            // 새로 들어온 데이터는 맨 위에 추가
            // NEW 마크는 순간적으로만 표시되도록 상태에 기록
            setNewIds((prevIds) => {
              const next = new Set(prevIds);
              next.add(updated.transferId);
              // 8초 후 자동 제거
              window.setTimeout(() => {
                setNewIds((inner) => {
                  const after = new Set(inner);
                  after.delete(updated.transferId);
                  return after;
                });
              }, 8000);
              return next;
            });
            return [updated, ...prev];
          }
          const next = prev.slice();
          next[idx] = { ...next[idx], ...updated };
          return next;
        });

        try {
          console.log("SSE 업데이트 수신:", updated);
          // 최고 입찰자 및 양도자 안내는 직접 클릭할 때만 표시하도록 제거
        } catch (e) {
          console.error("SSE 오류", e);
        }
      },
      onError: (e) => {
        console.error("SSE 오류", e);
      },
    });

    eventSourceRef.current = es;

    return () => {
      es.close();
    };
  }, [performanceId, isLoadedOnce, memberId]);

   const handleOpenModal = (transfer: Transfer) => {
    console.log("양도 티켓 클릭:", transfer);
    console.log("현재 최고 입찰자 ID:", transfer.highestBidderId);
    console.log("양도 티켓 판매자 ID:", transfer.transferSellerId);
    if (memberId && transfer.highestBidderId === memberId) {
      Swal.fire({
        icon: "error",
        text: "이미 최고 입찰자 입니다!",
      });
      return;
    }
    if (memberId && transfer.transferSellerId === memberId) {
      Swal.fire({
        icon: "error",
        text: "본인 양도 티켓에는 입찰을 할 수 없습니다!",
      });
      return;
    }

    setSelectedTransfer(transfer);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedTransfer(null);
  };

  if (!performanceId) {
    return (
      <div>공연 ID가 필요합니다. URL에 performanceId를 포함해 주세요.</div>
    );
  }

  return (
    <div className={styles.pageContainer}>
      {/* 리스트 영역 - 전체 폭 사용 */}
      <div className={styles.listContainer}>
        <div className={styles.titleSection}>
          <h2 className={styles.title}>양도 티켓 리스트</h2>
          <div className={styles.helpIconContainer}>
            <button
              className={styles.helpIcon}
              onClick={() => setShowTooltip(!showTooltip)}
              onBlur={() => setTimeout(() => setShowTooltip(false), 200)}
            >
              ?
            </button>
            {showTooltip && (
              <div className={styles.tooltip}>
                <div className={styles.tooltipContent}>
                  <h4>양도 안내</h4>
                  <p>실시간으로 업데이트되는 양도 티켓입니다.</p>
                  <ul>
                    <li><strong>NEW</strong>: 방금 등록된 티켓</li>
                    <li><strong>남은시간</strong>: 입찰 마감까지의 시간</li>
                    <li><strong>참여하기</strong>: 입찰 참여 버튼</li>
                  </ul>
                  <p className={styles.tooltipNote}>
                    ※ 최고 입찰자나 판매자는 해당 티켓에 다시 입찰할 수 없습니다.
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>
          
          {isLoading ? (
            <div className={styles.loadingContainer}>
              로딩 중...
            </div>
          ) : transfers.length === 0 ? (
            <div className={styles.loadingContainer}>
              등록된 양도 티켓이 없습니다.
            </div>
          ) : (
            <>
              {transfers.map((transfer) => (
                <TransferItem
                  key={transfer.transferId}
                  transfer={transfer}
                  onTransferClick={handleOpenModal}
                  isNew={newIds.has(transfer.transferId)}
                />
              ))}
              
              {totalPages > 1 && (
                <div className={styles.pagination}>
                  <button
                    onClick={() => setPage((p) => Math.max(p - 1, 0))}
                    disabled={page === 0}
                  >
                    이전
                  </button>
                  <span>
                    {page + 1} / {totalPages}
                  </span>
                  <button
                    onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
                    disabled={page >= totalPages - 1}
                  >
                    다음
                  </button>
                </div>
              )}
            </>
          )}
      </div>

      {selectedTransfer && (
        <TransferModal
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          transfer={selectedTransfer}
          userCoins={userCoins}
        />
      )}
    </div>
  );
};

export default TransferTicketList;
