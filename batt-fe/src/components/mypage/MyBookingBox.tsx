import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import styles from "../../styles/MyBookingBox.module.css";
import type { BookingListPage } from "../../types/BookingListPage";
import { fetchBookings } from "../../lib/api/bookings";
import { useAuthStore } from "../../stores/authStore";
import Swal from "sweetalert2";

const MyBookingBox: React.FC = () => {
  const [page, setPage] = useState(0);
  const navigate = useNavigate();
  const { memberId } = useAuthStore();
  const pageSize = 10;

  const formatDate = (timestamp: number | null | undefined): string => {
    if (!timestamp) return "-";

    try {
      let timestampMs = timestamp;
      if (timestamp < 10000000000) {
        timestampMs = timestamp * 1000;
      }

      const date = new Date(timestampMs);
      if (isNaN(date.getTime())) {
        return "-";
      }

      const formatter = new Intl.DateTimeFormat("ko-KR", {
        timeZone: "Asia/Seoul",
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      });
      const parts = formatter
        .formatToParts(date)
        .reduce<Record<string, string>>((acc, p: Intl.DateTimeFormatPart) => {
          acc[p.type] = p.value;
          return acc;
        }, {});
      return `${parts.year}-${parts.month}-${parts.day}`;
    } catch {
      return "-";
    }
  };

  const {
    data: bookingData,
    isLoading,
    isError,
    error,
  } = useQuery<BookingListPage, Error>({
    queryKey: ["bookings", memberId, page],
    queryFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return fetchBookings({ memberId, page, size: pageSize });
    },
    enabled: !!memberId,
    placeholderData: (previousData) => previousData,
  });

  const handleRowClick = (bookingId: number | string) => {
    const validBookingId =
      typeof bookingId === "number" && !isNaN(bookingId)
        ? bookingId
        : typeof bookingId === "string" && !isNaN(Number(bookingId))
          ? Number(bookingId)
          : null;

    if (!validBookingId) {
      Swal.fire({
        icon: "error",
        text: "예매 정보를 불러올 수 없습니다.",
      });
      return;
    }

    navigate(`/mypage/bookings/${validBookingId}`);
  };

  const renderPageNumbers = () => {
    if (!bookingData) return null;
    const pageNumbers = [];
    for (let i = 0; i < bookingData.pageInfo.totalPages; i++) {
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

  if (isLoading) return <div>로딩 중...</div>;
  if (isError) return <div>에러: {error.message}</div>;
  if (!bookingData) return <div>예매 내역을 불러올 수 없습니다.</div>;

  // 예매 내역이 비어있는 경우
  if (bookingData.bookings.length === 0) {
    return (
      <div className={styles.box}>
        <div className={styles.emptyState}>
          <p>아직 예매 내역이 없습니다!</p>
          <p>첫 공연을 예매해보세요 🎭</p>
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
            <th>예매일</th>
            <th>예매번호</th>
            <th>공연명</th>
            <th>관람일</th>
            <th>매수</th>
            <th>상태</th>
          </tr>
        </thead>
        <tbody>
          {bookingData.bookings.map((booking, index) => (
            <tr
              key={booking.bookingId || index}
              onClick={() => handleRowClick(booking.bookingId)}
              className={styles.clickableRow}
            >
              <td>{page * pageSize + index + 1}</td>
              <td>{formatDate(booking.bookingCreatedAt)}</td>
              <td>{booking.orderId}</td>
              <td>{booking.title}</td>
              <td>{booking.performanceDate}</td>
              <td>{booking.seatCount}매</td>
              <td>
                <span>
                  {(() => {
                    switch (booking.status) {
                      case "PENDING":
                        return "결제 대기 중";
                      case "CONFIRMED":
                        return "예매 완료";
                      case "TRANSFER_PENDING":
                        return "양도 진행 중";
                      case "USER_CANCELLED":
                        return "취소";
                      case "TRANSFER_FAILED_AUTO_CANCELLED":
                        return "양도 실패";
                      case "TRANSFERRED":
                        return "양도 완료";
                      default:
                        return booking.status;
                    }
                  })()}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className={styles.pagination}>
        <span onClick={() => setPage((prev) => Math.max(prev - 1, 0))}>
          &lt;
        </span>
        {renderPageNumbers()}
        <span
          onClick={() =>
            setPage((prev) =>
              Math.min(prev + 1, bookingData.pageInfo.totalPages - 1),
            )
          }
        >
          &gt;
        </span>
      </div>
    </div>
  );
};

export default MyBookingBox;
