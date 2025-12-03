import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import styles from "@styles/MyCoinBox.module.css";
import type { CoinPage } from "../../types/CoinPage";
import { fetchCoins } from "../../lib/api/coins";
import { useAuthStore } from "../../stores/authStore";

const ITEMS_PER_PAGE = 10;

const MyCoinBox = () => {
  const [page, setPage] = useState(0);
  const { memberId } = useAuthStore();

  const {
    data: coinData,
    isLoading,
    isError,
    error,
  } = useQuery<CoinPage, Error>({
    queryKey: ["coins", memberId, page],
    queryFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return fetchCoins({ memberId, page, size: ITEMS_PER_PAGE });
    },
    enabled: !!memberId,
    placeholderData: (previousData) => previousData,
  });

  const renderPageNumbers = () => {
    if (!coinData) return null;
    const pageNumbers = [];
    for (let i = 0; i < coinData.pageInfo.totalPages; i++) {
      pageNumbers.push(
        <span
          key={i}
          onClick={() => setPage(i)}
          className={page === i ? styles.activePage : ""}
        >
          {i + 1}
        </span>,
      );
    }
    return pageNumbers;
  };

  const formatDate = (timestamp: number) => {
    let timestampMs = timestamp;
    if (timestamp < 10000000000) {
      timestampMs = timestamp * 1000;
    }

    const date = new Date(timestampMs);
    if (isNaN(date.getTime())) {
      return "-";
    }

    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, "0")}.${String(date.getDate()).padStart(2, "0")}`;
  };

  const isEarnedStatus = (status: string): boolean => {
    const earnedStatuses = [
      "SIGN_UP",
      "REVIEW",
      "TRANSFER_SUCCESS",
      "BID_FAIL",
    ];
    return earnedStatuses.includes(status);
  };

  const getStatusDescription = (status: string): string => {
    switch (status) {
      case "SIGN_UP":
        return "회원가입";
      case "REVIEW":
        return "리뷰 작성";
      case "TRANSFER_SUCCESS":
        return "양도 성공";
      case "TRANSFER_FAIL":
        return "양도 실패";
      case "BID_SUCCESS":
        return "경매 입찰 성공";
      case "BID_FAIL":
        return "경매 입찰 취소";
      default:
        return status;
    }
  };

  if (isLoading) return <div>로딩 중...</div>;
  if (isError) return <div>에러: {error.message}</div>;
  if (!coinData) return <div>BATT 내역을 불러올 수 없습니다.</div>;

  if (coinData.coins.length === 0) {
    return (
      <div className={styles.box}>
        <div className={styles.emptyState}>
          <p>아직 BATT 내역이 없습니다!</p>
          <p>리뷰나 양도 활동으로 BATT를 적립해보세요 💰</p>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.box}>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>순서</th>
            <th>적립/사용</th>
            <th>사유</th>
            <th>날짜</th>
            <th>BATT 변동</th>
            <th>잔여 BATT</th>
          </tr>
        </thead>
        <tbody>
          {coinData.coins.map((item, index) => {
            const isEarned = isEarnedStatus(item.status);
            const amount = Math.abs(item.amount);

            return (
              <tr key={item.id}>
                <td>{page * ITEMS_PER_PAGE + index + 1}</td>
                <td>
                  <span
                    className={`${styles.tag} ${
                      isEarned ? styles.earned : styles.used
                    }`}
                  >
                    {isEarned ? "적립" : "사용"}
                  </span>
                </td>
                <td>{getStatusDescription(item.status)}</td>
                <td>{formatDate(item.logCreatedAt)}</td>
                <td className={isEarned ? styles.plus : styles.minus}>
                  {isEarned ? "+" : "−"} {amount.toLocaleString()} C
                </td>
                <td>
                  {item.remainCoin !== null && item.remainCoin !== undefined
                    ? `${item.remainCoin.toLocaleString()} C`
                    : "-"}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
      <div className={styles.pagination}>
        <button
          onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
          disabled={page === 0}
        >
          &lt;
        </button>
        {renderPageNumbers()}
        <button
          onClick={() =>
            setPage((prev) =>
              coinData
                ? Math.min(prev + 1, coinData.pageInfo.totalPages - 1)
                : prev,
            )
          }
          disabled={!coinData || page === coinData.pageInfo.totalPages - 1}
        >
          &gt;
        </button>
      </div>
    </div>
  );
};

export default MyCoinBox;
