import React, { useState, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import styles from "@styles/BookingDetailBox.module.css";
import type { BookingDetail } from "../../types/BookingDetail";
import type { Seat } from "../../types/Seat";
import {
  fetchBookingDetail,
  cancelBooking,
  createTransfer,
} from "../../lib/api/bookings";
import {
  createReview,
  fetchReviewInfo,
  updateReview,
  type ReviewInfo,
  type CreateReviewResponse,
} from "../../lib/api/reviews";
import StarRating from "../common/StarRating";
import TransferModal from "../common/TransferModal";
import CancelModal from "../common/CancelModal";
import { useAuthStore } from "../../stores/authStore";
import Swal from "sweetalert2";
import { useNavigate } from "react-router-dom";
import type { BookingInfo } from "../../types/BookingInfo";
import type { User } from "../../types/User";
import { fetchUserInfo } from "../../lib/api/members";

interface BookingDetailBoxProps {
  bookingId: number;
}

const BookingDetailBox: React.FC<BookingDetailBoxProps> = ({ bookingId }) => {
  const [reviewContent, setReviewContent] = useState("");
  const [rating, setRating] = useState(0);
  const [reviewInfo, setReviewInfo] = useState<ReviewInfo | null>(null);
  const [isTransferModalOpen, setIsTransferModalOpen] = useState(false);
  const [isCancelModalOpen, setIsCancelModalOpen] = useState(false);
  const { memberId } = useAuthStore();
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  

  const formatDateTime = (timestamp: number | null | undefined): string => {
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
        hour: "2-digit",
        minute: "2-digit",
        hour12: false,
      });
      const parts = formatter
        .formatToParts(date)
        .reduce<Record<string, string>>((acc, p: Intl.DateTimeFormatPart) => {
          acc[p.type] = p.value;
          return acc;
        }, {});
      return `${parts.year}-${parts.month}-${parts.day} ${parts.hour}:${parts.minute}`;
    } catch {
      return "-";
    }
  };

  const {
    data: booking,
    isLoading,
    isError,
    error,
  } = useQuery<BookingDetail, Error>({
    queryKey: ["bookingDetail", memberId, bookingId],
    queryFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return fetchBookingDetail({ memberId, bookingId });
    },
    enabled: !!memberId,
  });
    const {
      data: userInfo,
      isLoading: isUserLoading,
      isError: isUserError,
    } = useQuery<User, Error>({
      queryKey: ["userInfo", memberId],
      queryFn: () => {
        if (!memberId) throw new Error("사용자 정보가 없습니다.");
        return fetchUserInfo(memberId);
      },
      enabled: !!memberId,
    });

  // 리뷰 정보 조회 (booking 데이터가 로드된 후에 실행)
  const { data: reviewData } = useQuery({
    queryKey: ["reviewInfo", memberId, bookingId],
    queryFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return fetchReviewInfo(memberId, bookingId);
    },
    enabled:
      !!memberId &&
      !!booking &&
      booking.status === "CONFIRMED" &&
      new Date(booking.performanceDate) < new Date(),
  });

  // 리뷰 데이터가 로드되면 상태 업데이트
  useEffect(() => {
    if (reviewData?.reviewInfo.hasReviewed) {
      setReviewInfo(reviewData.reviewInfo);
      setReviewContent(reviewData.reviewInfo.content);
      setRating(reviewData.reviewInfo.rating);
    }
  }, [reviewData]);

  const createReviewMutation = useMutation<CreateReviewResponse>({
    mutationFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return createReview(memberId, bookingId, {
        rating: rating,
        content: reviewContent,
      });
    },
    onSuccess: (data) => {
      Swal.fire({
        icon: "success",
        text: "리뷰가 작성되었습니다.",
      });

      if (data && data.remainCoin !== undefined) {
        useAuthStore.getState().setCoin(data.remainCoin);
      }

      // 리뷰 정보 새로고침
      queryClient.invalidateQueries({
        queryKey: ["reviewInfo", memberId, bookingId],
      });
    },
    onError: (error) => {
      console.error("리뷰 작성 실패:", error);
      Swal.fire({
        icon: "error",
        text: "리뷰 작성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
      });
    },
  });

  const updateReviewMutation = useMutation({
    mutationFn: () => {
      if (!reviewInfo) throw new Error("리뷰 정보가 없습니다.");
      return updateReview(reviewInfo.reviewId, {
        rating: rating,
        content: reviewContent,
      });
    },
    onSuccess: () => {
      Swal.fire({
        icon: "success",
        text: "리뷰가 수정되었습니다.",
      });

      // 리뷰 정보 새로고침
      queryClient.invalidateQueries({
        queryKey: ["reviewInfo", memberId, bookingId],
      });
    },
    onError: (error) => {
      console.error("리뷰 수정 실패:", error);
      Swal.fire({
        icon: "error",
        text: "리뷰 수정 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
      });
    },
  });

  const cancelBookingMutation = useMutation({
    mutationFn: (cancelReason: string) =>
      cancelBooking(bookingId, cancelReason, booking?.totalAmount || 0),
    onSuccess: async () => {
      Swal.fire({
        icon: "success",
        text: "예매가 취소되었습니다.",
      });
      setIsCancelModalOpen(false);

      await queryClient.invalidateQueries({
        queryKey: ["bookingDetail", memberId, bookingId],
      });
      await queryClient.invalidateQueries({ queryKey: ["bookings", memberId] });

      await queryClient.refetchQueries({
        queryKey: ["bookingDetail", memberId, bookingId],
      });
    },
    onError: (error) => {
      console.error("예매 취소 실패:", error);
      Swal.fire({
        icon: "error",
        text: "예매 취소 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
      });
    },
  });

  const createTransferMutation = useMutation({
    mutationFn: (transferEndDateTime: number) =>
      createTransfer(bookingId, { transferEndDateTime }),
    onSuccess: async () => {
      Swal.fire({
        icon: "success",
        text: "양도 신청이 완료되었습니다.",
      });
      setIsTransferModalOpen(false);
      await queryClient.invalidateQueries({
        queryKey: ["bookingDetail", memberId, bookingId],
      });
      await queryClient.invalidateQueries({ queryKey: ["bookings", memberId] });
      await queryClient.refetchQueries({
        queryKey: ["bookingDetail", memberId, bookingId],
      });
    },
    onError: (error) => {
      console.error("양도 신청 실패:", error);
      Swal.fire({
        icon: "error",
        text: "양도 신청 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
      });
    },
  });

  if (isLoading || isUserLoading) {
    return <div>로딩 중...</div>;
  }

  if (isError || isUserError) {
    return <div>에러: {error?.message}</div>;
  }

  if (!booking) {
    return <div>예매 정보를 찾을 수 없습니다.</div>;
  }

  const isPastPerformance = new Date(booking.performanceDate) < new Date();

  const isCancellable = () => {
    if (booking.status !== "CONFIRMED") return false;

    const performanceDate = new Date(booking.performanceDate);
    const dayBeforePerformance = new Date(performanceDate);
    dayBeforePerformance.setDate(performanceDate.getDate() - 1);
    dayBeforePerformance.setHours(23, 59, 59, 999);

    return new Date() <= dayBeforePerformance;
  };

  const cancellableUntil = () => {
    const performanceDate = new Date(booking.performanceDate);
    const dayBefore = new Date(performanceDate);
    dayBefore.setDate(performanceDate.getDate() - 1);
    return dayBefore.toISOString().split("T")[0] + " 23:59";
  };

  const getStatusText = (status: string) => {
    switch (status) {
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
        return status;
    }
  };

  const handleReviewSubmit = () => {
    if (!reviewContent.trim()) {
      Swal.fire({
        icon: "error",
        text: "리뷰 내용을 입력해주세요.",
      });
      return;
    }
    if (rating === 0) {
      Swal.fire({
        icon: "error",
        text: "별점을 선택해주세요.",
      });
      return;
    }

    if (reviewInfo && reviewInfo.hasReviewed) {
      updateReviewMutation.mutate();
    } else {
      createReviewMutation.mutate();
    }
  };

  const info : BookingInfo = {
    bookingId: bookingId,
    performance: booking.title,
    date: booking.performanceDate,
    time: booking.performanceTime,
    theater: booking.venueName,
    selectedSeats: booking.seatDetails,
    price: booking.totalAmount,
    scheduleId: booking.performanceScheduleId
  }

  const goToPayment = function () {
    navigate("/payment", { state: { bookingData: info, userInfo: userInfo } });
  };

  return (
    <div className={styles.box}>
      <div className={styles.infoBox}>
        <img
          src={booking.posterImageUrl}
          alt={booking.title}
          className={styles.poster}
        />
        <div className={styles.details}>
          <h2>{booking.title}</h2>
          <div className={styles.grid}>
            <span>예매 번호</span>
            <strong>{booking.orderId}</strong>
            <span>공연장</span>
            <span>{booking.venueName}</span>
            <span>예매 일시</span>
            <span>{formatDateTime(booking.bookingCreatedAt)}</span>
            <span>예매자</span>
            <span>{booking.memberName}</span>
            <span>관람 일시</span>
            <span>
              {booking.performanceDate} {booking.performanceTime}
            </span>
            <span>취소 가능</span>
            <span>
              {isCancellable() ? `${cancellableUntil()} 까지` : "불가능"}
            </span>
            <span>매수</span>
            <span>{booking.seatCount}매</span>
            <span>상태</span>
            <span>{getStatusText(booking.status)}</span>
          </div>
        </div>
      </div>

      <h3 className={styles.sectionTitle}>결제 정보</h3>
      <div className={styles.paymentGrid}>
        <span>결제 수단</span>
        <span>{booking.paymentMethod}</span>
        <span>결제 금액</span>
        <span>{booking.totalAmount.toLocaleString()}원</span>
      </div>

      <h3 className={styles.sectionTitle}>좌석 정보</h3>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>예매 번호</th>
            <th>등급</th>
            <th>좌석</th>
            <th>가격</th>
          </tr>
        </thead>
        <tbody>
          {booking.seatDetails.map((seat: Seat, index: number) => (
            <tr key={index}>
              <td>{seat.bookingNumber}</td>
              <td>{seat.grade}</td>
              <td>{seat.seatNumber}</td>
              <td>{seat.price.toLocaleString()}원</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className={styles.actions}>
        {isCancellable() && !isPastPerformance && (
          <>
            <button
              className={styles.secondaryButton}
              onClick={() => setIsTransferModalOpen(true)}
            >
              양도하기
            </button>
            <button
              className={styles.primaryButton}
              onClick={() => setIsCancelModalOpen(true)}
            >
              취소하기
            </button>
          </>
        )}
        {booking.status === "PENDING" && (
          <button
            className={styles.primaryButton}
            onClick={() => {
              goToPayment();
              // Swal.fire("결제 기능은 추후 구현 예정입니다.");
            }}
          >
            결제하기
          </button>
        )}
        {booking.status === "CONFIRMED" && isPastPerformance && (
          <div className={styles.reviewSection}>
            <div className={styles.starRatingWrapper}>
              <StarRating rating={rating} setRating={setRating} size="large" />
            </div>
            <textarea
              className={styles.reviewTextarea}
              value={reviewContent}
              maxLength={150}
              onChange={(e) => setReviewContent(e.target.value)}
              placeholder={
                reviewData?.reviewInfo.hasReviewed
                  ? "리뷰를 수정해주세요."
                  : "리뷰를 작성해주세요."
              }
            />
            <button
              onClick={handleReviewSubmit}
              className={styles.primaryButton}
              disabled={
                createReviewMutation.isPending || updateReviewMutation.isPending
              }
            >
              {createReviewMutation.isPending || updateReviewMutation.isPending
                ? "처리 중..."
                : reviewData?.reviewInfo.hasReviewed
                  ? "리뷰 수정하기"
                  : "리뷰 작성하기"}
            </button>
          </div>
        )}
      </div>

      <TransferModal
        isOpen={isTransferModalOpen}
        onClose={() => setIsTransferModalOpen(false)}
        onConfirm={(transferEndDateTime) =>
          createTransferMutation.mutate(transferEndDateTime)
        }
        performanceDate={booking.performanceDate}
        bookingCreatedAt={booking.bookingCreatedAt}
        isLoading={createTransferMutation.isPending}
      />

      <CancelModal
        isOpen={isCancelModalOpen}
        onClose={() => setIsCancelModalOpen(false)}
        onConfirm={(cancelReason: string) =>
          cancelBookingMutation.mutate(cancelReason)
        }
        bookingInfo={{
          title: booking.title,
          performanceDate: booking.performanceDate,
          performanceTime: booking.performanceTime,
          totalAmount: booking.totalAmount,
          seatCount: booking.seatCount,
        }}
        bookingCreatedAt={booking.bookingCreatedAt}
        isLoading={cancelBookingMutation.isPending}
      />
    </div>
  );
};

export default BookingDetailBox;
