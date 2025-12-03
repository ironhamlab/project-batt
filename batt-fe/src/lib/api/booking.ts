import type { SeatCoordinate } from "../../types/SeatCoordinate.ts";
import apiClient from "../axios.ts";

const axios = apiClient;

interface SeatApiResponse {
  seatId: number;
  grade: string;
  number: string;
  price: number;
  status: boolean;
  heldByUserId: string;
  x: number;
  y: number;
}
interface SeatHoldSuccessResponse {
  heldSeatIds: number[];
  failedSeatIds: number[];
  holdExpiresAt: string;
}
interface SeatHoldFailResponse {
  httpStatus: string;
  code: string;
}
interface SeatReleaseApiResponse {
  releasedSeatIds: number[];
  failedSeatIds: number[];
}
type SeatHoldApiResponse = SeatHoldSuccessResponse | SeatHoldFailResponse;
const resToSeat = (apiRes: SeatApiResponse): SeatCoordinate => {
  return {
    id: apiRes.seatId,
    seat: {
      seatId: apiRes.seatId,
      seatNumber: apiRes.number,
      grade: apiRes.grade as "VIP" | "R" | "S" | "A" | "B",
      price: apiRes.price,
      bookingNumber: "",
    },
    x: apiRes.x,
    y: apiRes.y,
    status: apiRes.status,
  };
};

export async function getSeats(scheduleId: number) {
  const res = await axios.get<SeatApiResponse[]>(
    `/api/v1/performance-schedules/${scheduleId}/seats`,
  );
  return res.data.map(resToSeat);
}

export async function holdSeats(
  scheduleId: number,
  memberId: number,
  seats: number[],
) {
  try {
    const res = await axios.post<SeatHoldApiResponse>(
      `/api/v1/performance-schedules/${scheduleId}/seats/hold/${memberId}`,
      { seatIds: seats },
    );
    if ("code" in res.data && res.data.code === "BATT703") {
      return { success: false, code: res.data.code };
    } else {
      const successData = res.data as SeatHoldSuccessResponse;
      return { success: true, data: successData };
    }
  } catch (error) {
    console.error(error);
    return { success: false, error };
  }
}

export async function releaseSeats(
  scheduleId:number,
  memberId: number,
  seats: number[]
) {
  const res = await axios.post<SeatReleaseApiResponse>(
    `/api/v1/performance-schedules/${scheduleId}/seats/release/${memberId}`,
    {seatIds: seats}
  )
  return res.data
}

export async function createOrderId() {
  const res = await axios.get<{ reservationId: string }>(
    "/api/v1/bookings/snowflake/generate",
  );
  return res.data.reservationId;
}
