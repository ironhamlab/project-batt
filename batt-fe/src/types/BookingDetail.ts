import type { Seat } from "./Seat";

export interface BookingDetail {
  posterImageUrl: string;
  title: string;
  venueName: string;
  orderId: string;
  bookingCreatedAt: number; // timestamp로 변경
  performanceDate: string;
  performanceTime: string;
  performanceScheduleId: number;
  cancellationDeadline: string;
  memberName: string;
  status:
    | "PENDING"
    | "CONFIRMED"
    | "TRANSFER_PENDING"
    | "USER_CANCELLED"
    | "TRANSFER_FAILED_AUTO_CANCELLED"
    | "TRANSFERRED";
  paymentMethod: string;
  totalAmount: number;
  seatCount: number;
  seatDetails: Seat[];
}
