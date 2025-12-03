export interface Transfer {
  transferId: number;
  status: "ACTIVE" | "ENDED";
  currentHighestBid: number;
  price: number;
  seatNumber: string[];
  performanceDate?: string;
  performanceTime?: string;
  transferEndDateTime: number;
  highestBidderId?: number;
  transferSellerId?: number;
}
