import apiClient from "../axios";
import type { BookingListPage } from "../../types/BookingListPage";
import type { BookingDetail } from "../../types/BookingDetail";

interface FetchBookingsParams {
  memberId: number;
  page: number;
  size: number;
}

export const fetchBookings = async ({
  memberId,
  page,
  size,
}: FetchBookingsParams): Promise<BookingListPage> => {
  const { data } = await apiClient.get(
    `/api/v1/bookings?memberId=${memberId}&page=${page}&size=${size}`,
  );
  return data;
};

interface FetchBookingDetailParams {
  memberId: number;
  bookingId: number;
}

export const fetchBookingDetail = async ({
  memberId,
  bookingId,
}: FetchBookingDetailParams): Promise<BookingDetail> => {
  const { data } = await apiClient.get(
    `/api/v1/bookings?memberId=${memberId}&bookingId=${bookingId}`,
  );
  return data;
};

// 결제 취소 API
interface CancelPaymentRequest {
  bookingId: number;
  cancelReason: string;
  cancelAmount: number;
}

export const cancelBooking = async (
  bookingId: number,
  cancelReason: string,
  cancelAmount: number,
): Promise<void> => {
  const requestData: CancelPaymentRequest = {
    bookingId,
    cancelReason,
    cancelAmount,
  };
  await apiClient.post("/api/v1/payments/cancel", requestData);
};

interface CreateTransferRequest {
  transferEndDateTime: number;
}

export const createTransfer = async (
  bookingId: number,
  transferData: CreateTransferRequest,
): Promise<void> => {
  await apiClient.post(`/api/v1/bookings/${bookingId}/transfers`, transferData);
  console.log(new Date(transferData.transferEndDateTime * 1000).toISOString());
};
