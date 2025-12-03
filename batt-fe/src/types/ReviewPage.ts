import type { Review } from "./Review";

export interface PageInfo {
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface ReviewPage {
  pageInfo: PageInfo;
  reviews: Review[];
}

export interface PerformanceReviewPage extends ReviewPage {
  averageRating: number;
}
