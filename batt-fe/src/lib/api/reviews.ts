import apiClient from "../axios";
import type { ReviewPage, PerformanceReviewPage } from "../../types/ReviewPage";

export interface FetchReviewsParams {
  memberId: number;
  page: number;
  size: number;
}

export interface CreateReviewRequest {
  rating: number;
  content: string;
}

export interface CreateReviewResponse {
  remainCoin: number;
}

export interface UpdateReviewRequest {
  rating: number;
  content: string;
}

export interface ReviewInfo {
  reviewId: number;
  rating: number;
  content: string;
  hasReviewed: boolean;
}

export interface ReviewInfoResponse {
  reviewInfo: ReviewInfo;
}

export const fetchReviews = async ({
  memberId,
  page,
  size,
}: FetchReviewsParams): Promise<ReviewPage> => {
  const { data } = await apiClient.get(
    `/api/v1/reviews?memberId=${memberId}&page=${page}&size=${size}`,
  );
  return data;
};

export const createReview = async (
  memberId: number,
  bookingId: number,
  reviewData: CreateReviewRequest,
): Promise<CreateReviewResponse> => {
  const { data } = await apiClient.post(
    `/api/v1/members/${memberId}/bookings/${bookingId}/reviews`,
    reviewData,
  );
  return data;
};

// 리뷰 정보 조회
export const fetchReviewInfo = async (
  memberId: number,
  bookingId: number,
): Promise<ReviewInfoResponse> => {
  const { data } = await apiClient.get(
    `/api/v1/reviews?memberId=${memberId}&bookingId=${bookingId}`,
  );
  return data;
};

// 리뷰 수정 (PATCH)
export const updateReview = async (
  reviewId: number,
  reviewData: UpdateReviewRequest,
): Promise<void> => {
  await apiClient.patch(`/api/v1/reviews/${reviewId}`, reviewData);
};

// 기존 PUT 메소드 (호환성을 위해 유지)
export const updateReviewPut = async (
  reviewId: number,
  reviewData: UpdateReviewRequest,
): Promise<void> => {
  await apiClient.put(`/api/v1/reviews/${reviewId}`, reviewData);
};

export interface FetchReviewsByPerformanceParams {
  performanceId: number;
  page: number;
  size: number;
}

export const fetchReviewsByPerformance = async ({
  performanceId,
  page,
  size,
}: FetchReviewsByPerformanceParams): Promise<PerformanceReviewPage> => {
  const { data } = await apiClient.get(
    `/api/v1/reviews?performanceId=${performanceId}&page=${page}&size=${size}`,
  );
  return data;
};
