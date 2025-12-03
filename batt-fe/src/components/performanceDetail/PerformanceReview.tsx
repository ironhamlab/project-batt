import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import styles from "@styles/PerformanceReview.module.css";
import ReviewModal from "./ReviewModal";
import StarRating from "../common/StarRating";
import { fetchReviewsByPerformance } from "../../lib/api/reviews";
import type { PerformanceReviewPage } from "../../types/ReviewPage";

interface PerformanceReviewProps {
  performanceId: number;
}

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

const PerformanceReview: React.FC<PerformanceReviewProps> = ({
  performanceId,
}) => {
  const [currentPage, setCurrentPage] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const reviewsPerPage = 10;

  const {
    data: reviewData,
    isLoading,
    isError,
  } = useQuery<PerformanceReviewPage, Error>({
    queryKey: ["performanceReviews", performanceId, currentPage],
    queryFn: () =>
      fetchReviewsByPerformance({
        performanceId,
        page: currentPage,
        size: reviewsPerPage,
      }),
    enabled: !!performanceId,
    placeholderData: (previousData) => previousData,
  });

  const averageRating = reviewData?.averageRating ?? 0;
  const totalReviews = reviewData?.pageInfo.totalElements ?? 0;
  const reviews = reviewData?.reviews ?? [];

  const renderPageNumbers = () => {
    if (!reviewData) return null;
    const pageNumbers = [];
    const totalPages = reviewData.pageInfo.totalPages;
    for (let i = 0; i < totalPages; i++) {
      pageNumbers.push(
        <button
          key={i}
          onClick={() => setCurrentPage(i)}
          className={currentPage === i ? styles.activePage : ""}
        >
          {i + 1}
        </button>,
      );
    }
    return pageNumbers;
  };

  if (isLoading) {
    return <div className={styles.container}>리뷰를 불러오는 중...</div>;
  }

  if (isError) {
    return (
      <div className={styles.container}>리뷰를 불러오는 데 실패했습니다.</div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.notice}>
        <p className={styles.noticeTitle}>꼭 읽어주세요</p>
        <button
          onClick={() => setIsModalOpen(true)}
          className={styles.noticeButton}
        >
          운영규정 자세히 보기 ▶
        </button>
      </div>
      <p className={styles.noticeContent}>
        게시판 운영 규정에 어긋난다고 판단되는 게시글은 사전 통보없이 블라인드
        처리될 수 있습니다. <br />
        특히 티켓 매매 및 양도의 글은 발견 즉시 임의 삭제되며 전화번호, 이메일
        등의 개인정보는 악용될 우려가 있으므로 게시를 삼가 주시기 바랍니다.{" "}
        <br />
        사전 경고에도 불구하고 불량 게시물을 계속적으로 게재한 게시자의 경우
        BATT 티켓 게시판 작성 권한이 제한됩니다.
      </p>

      <div className={styles.ratingSection}>
        <h3>관람 평점</h3>
        <div className={styles.stars}>
          <StarRating rating={averageRating} readOnly={true} size="large" />
        </div>
        <span className={styles.ratingValue}>
          {averageRating.toFixed(1)} / 10
        </span>
        <p>총 {totalReviews} 개</p>
      </div>

      <hr className={styles.divider} />

      <div className={styles.reviewList}>
        {reviews.length > 0 ? (
          reviews.map((review, index) => {
            // 이메일 마스킹: 앞 3글자 + *****
            const maskedEmail =
              review.email.length >= 3
                ? review.email.substring(0, 3) + "*****"
                : review.email;

            return (
              <div
                key={review.id || `review-${index}`}
                className={styles.reviewCard}
              >
                <div className={styles.reviewCardHeader}>
                  <div className={styles.reviewRating}>
                    <StarRating rating={review.rating} readOnly={true} />
                  </div>
                  <div className={styles.reviewMeta}>
                    <span>{maskedEmail}</span> |{" "}
                    <span>{formatDateTime(review.createdAt)}</span>
                  </div>
                </div>
                <p className={styles.reviewContent}>{review.content}</p>
              </div>
            );
          })
        ) : (
          <div className={styles.emptyReview}>아직 등록된 리뷰가 없습니다!</div>
        )}
      </div>

      {totalReviews > 0 && (
        <div className={styles.pagination}>
          <button
            onClick={() => setCurrentPage(0)}
            disabled={currentPage === 0}
          >
            &lt;&lt;
          </button>
          <button
            onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
            disabled={currentPage === 0}
          >
            &lt;
          </button>
          {renderPageNumbers()}
          <button
            onClick={() =>
              setCurrentPage((p) => {
                const totalPages = reviewData?.pageInfo.totalPages ?? 1;
                return Math.min(totalPages - 1, p + 1);
              })
            }
            disabled={
              !reviewData || currentPage === reviewData.pageInfo.totalPages - 1
            }
          >
            &gt;
          </button>
          <button
            onClick={() =>
              setCurrentPage((reviewData?.pageInfo.totalPages ?? 1) - 1)
            }
            disabled={
              !reviewData || currentPage === reviewData.pageInfo.totalPages - 1
            }
          >
            &gt;&gt;
          </button>
        </div>
      )}

      {/* 운영규정 모달 */}
      <ReviewModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <h2>게시판 운영 규정</h2>
        <p>
          <strong>제1조 (목적)</strong> 본 규정은 BATT 리뷰 게시판의 건전한 이용
          환경 조성을 목적으로 합니다.
        </p>
        <p>
          <strong>제2조 (게시물 제한)</strong> 다음 각 호에 해당하는 게시물은
          사전 통보 없이 삭제 또는 블라인드 처리될 수 있습니다.
        </p>
        <ul>
          <li>타인에 대한 비방, 욕설, 명예훼손을 포함하는 내용</li>
          <li>상업적 광고, 홍보, 스팸성 내용</li>
          <li>티켓 매매, 양도, 교환 등 개인 간 거래를 유도하는 내용</li>
          <li>개인정보(전화번호, 이메일, 주소 등)를 포함하는 내용</li>
          <li>음란물 또는 청소년에게 유해한 내용</li>
          <li>기타 관련 법령 및 사회 통념에 어긋나는 내용</li>
        </ul>
        <p>
          <strong>제3조 (이용자 제재)</strong> 규정을 반복적으로 위반하는
          이용자는 경고 없이 게시판 이용 권한이 영구적으로 제한될 수 있습니다.
        </p>
      </ReviewModal>
    </div>
  );
};

export default PerformanceReview;
