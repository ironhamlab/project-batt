import { Divider, Typography } from "@mui/joy";
import type { BookingInfo } from "../../../../types/BookingInfo";
import NextButton from "../../ChoosingSeats/atom/NextButton";

interface Props {
  bookingInfo: BookingInfo;
  isAllChecked: boolean;
  handleNext: () => void;
  leftTime: number;
}
export default function TicketInfo({
  bookingInfo,
  isAllChecked,
  handleNext,
  leftTime,
}: Props) {
  const formatTime = (ms: number) => {
    const totalSeconds = Math.floor(ms / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
  };
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        height: "100%",
        gap: "20px",
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: "column",
        }}
      >
        <h2
          style={{
            margin: "20px 0",
          }}
        >
          티켓 주문 상세
        </h2>
        <h4>{bookingInfo.performance}</h4>
        <p>
          {bookingInfo.date} {bookingInfo.time}
        </p>
        <p>{bookingInfo.theater}</p>
      </div>
      <Divider orientation="horizontal" />
      <div
        style={{
          flexGrow: 1,
        }}
      >
        <h4>
          총 <span>{bookingInfo.selectedSeats.length}</span>석 선택
        </h4>

        {bookingInfo.selectedSeats.map((seat) => (
          <p key={seat.seatId}>{seat.seatNumber}</p>
        ))}
      </div>
      <div>
        <h4>결제 정보</h4>
        {/* <div>
          <p>티켓 가격</p>
          <p>예매 수수료</p>

        </div> */}
        <h3>총 {bookingInfo.price}원</h3>
      </div>

      <div
        style={{
          bottom: "0",
          width: "100%",
          left: "0",
          right: "0",
          padding: "0 20px",
          display: "flex",
          flexDirection: "row",
          justifyContent: "center",
          marginTop: "20px",
          marginBottom: "20px",
          gap: "10px",
        }}
      >
        <Typography level="title-lg" color="neutral">
          결제 남은 시간 :
        </Typography>
        <Typography
          level="title-lg"
          color={leftTime < 60000 ? "danger" : "primary"}
        >
          {formatTime(leftTime)}
        </Typography>
      </div>
      <NextButton isAble={isAllChecked} onClick={handleNext} text="결제하기" />
    </div>
  );
}
