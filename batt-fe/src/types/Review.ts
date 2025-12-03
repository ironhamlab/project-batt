export interface Review {
  id: number;
  memberId: number;
  bookingId: number;
  performanceId: number;
  performanceTitle: string;
  email: string;
  rating: number;
  content: string;
  createdAt: number;
}
