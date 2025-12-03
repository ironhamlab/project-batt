export interface PerformanceData {
  id: number;
  title: string;
  durationMinute: number;
  genre: string;
  venueName: string;
  venueAddress: string;
  ageRestriction: number;
  posterImageUrl: string;
  descriptionUrl: string;
  status: string;
  createdAt: string;
  performanceStartDate: string;
  performanceEndDate: string;
  prices: {
    VIP?: number;
    R?: number;
    S?: number;
    A?: number;
    B?: number;
  };
}
