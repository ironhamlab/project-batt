import type { Seat } from "./Seat";

export interface SeatCoordinate {
  id: number;
  seat: Seat;
  x: number;
  y: number;
  status: boolean;
}
