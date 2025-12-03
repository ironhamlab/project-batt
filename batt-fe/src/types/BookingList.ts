export interface BookingList {
  bookingId: number;
  title: string;
  orderId: string;
  performanceDate: string;
  seatCount: number;
  venueName: string;
  status:
    | "PENDING"
    | "CONFIRMED"
    | "TRANSFER_PENDING"
    | "USER_CANCELLED"
    | "TRANSFER_FAILED_AUTO_CANCELLED"
    | "TRANSFERRED";
  bookingCreatedAt: number;
}
