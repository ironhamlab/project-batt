import React, { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import styles from "../../styles/MyReviewBox.module.css";
import type { ReviewPage } from "../../types/ReviewPage";
import { fetchReviews } from "../../lib/api/reviews";
import StarRating from "../common/StarRating";
import { useAuthStore } from "../../stores/authStore";

const MyReviewBox: React.FC = () => {
  const [page, setPage] = useState(0);
  const { memberId } = useAuthStore();
  const pageSize = 5;

  const {
    data: reviewData,
    isLoading,
    isError,
    error,
  } = useQuery<ReviewPage, Error>({
    queryKey: ["reviews", memberId, page],
    queryFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return fetchReviews({ memberId, page, size: pageSize });
    },
    enabled: !!memberId,
    placeholderData: (previousData) => previousData,
  });

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

  const renderPageNumbers = () => {
    if (!reviewData) return null;
    const pageNumbers = [];
    for (let i = 0; i < reviewData.pageInfo.totalPages; i++) {
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
  if (!reviewData) return <div>리뷰 데이터를 불러올 수 없습니다.</div>;

  if (reviewData.reviews.length === 0) {
    return (
      <div className={styles.box}>
        <div className={styles.emptyState}>
          <p>아직 리뷰 내역이 없습니다!</p>
          <p>공연 관람 후 첫 리뷰를 작성해보세요 ⭐</p>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.box}>
      <div className={styles.header}>
        <p>*수정 후 운영 정책에 따라 포인트가 회수 될 수 있습니다.</p>
      </div>
      <hr className={styles.divider} />
      {reviewData?.reviews.map((review) => (
        <div key={review.id} className={styles.reviewItem}>
          <div className={styles.reviewHeader}>
            <span className={styles.performanceTitle}>
              {review.performanceTitle}{" "}
              <StarRating rating={review.rating} readOnly={true} size="small" />
            </span>
            <div className={styles.reviewMeta}>
              <span className={styles.createdAt}>
                {formatDate(review.createdAt)}
              </span>
            </div>
          </div>
          <p className={styles.reviewContent}>{review.content}</p>
        </div>
      ))}
      <div className={styles.pagination}>
        <span onClick={() => setPage((prev) => Math.max(prev - 1, 0))}>
          &lt;
        </span>
        {renderPageNumbers()}
        <span
          onClick={() =>
            setPage((prev) =>
              Math.min(prev + 1, reviewData.pageInfo.totalPages - 1),
            )
          }
        >
          &gt;
        </span>
      </div>
    </div>
  );
};

export default MyReviewBox;
