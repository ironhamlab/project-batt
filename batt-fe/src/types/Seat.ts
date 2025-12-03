export interface Seat {
  bookingNumber: string;
  seatNumber: string;
  grade: "VIP" | "R" | "S" | "A" | "B";
  price: number;
  seatId: number;
}
