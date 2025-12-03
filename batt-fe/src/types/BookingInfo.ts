import type { Seat } from "./Seat";

export interface BookingInfo {
  bookingId: number | null;
  performance: string;
  date: string;
  time: string;
  theater: string;
  selectedSeats: Seat[];
  price: number;
  scheduleId: number;
}
