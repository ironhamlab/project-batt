import type { BookingList } from "./BookingList";
import type { PageInfo } from "./ReviewPage"; // 기존에 정의한 PageInfo 재사용

export interface BookingListPage {
  pageInfo: PageInfo;
  bookings: BookingList[];
}
