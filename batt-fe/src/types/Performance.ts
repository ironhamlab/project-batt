export type Performance = {
  id: number;
  title: string;
  venueName: string;
  posterImageUrl: string;
  status: string;
  bookingOpenDate: string;
  performanceStartDate: string;
  performanceEndDate: string;
};

export type TransferPerformance = Performance & {
  transferTicketCount: number;
  performanceId: number;
};

// 미정.
export type CarouselImage = {
  id: number;
  title: string;
  ulr: string;
  alt: string;
};
