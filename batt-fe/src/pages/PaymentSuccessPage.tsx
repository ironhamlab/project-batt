import { useEffect, useRef, useState } from "react";
import {  useNavigate, useSearchParams } from "react-router-dom";
import style from "../styles/Payment.module.css";
import apiClient from "../lib/axios";
import type { BookingInfo } from "../types/BookingInfo";
import type { User } from "../types/User";
import { Button } from "@mui/joy";

interface paymentResponseData {
  bookingId: number;
  orderID: string;
  bookingNumber: string[];
  paymentStatus: string;
}
function getSessionData<T>(key: string): T | null {
  const sessionData = sessionStorage.getItem(key);
  if (!sessionData) {
    console.error("sessionData JSON parse error:");
    return null;
  }
  return JSON.parse(sessionData) as T;
}
export function PaymentSuccessPage() {
  const axios = apiClient;
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [responseData, setResponseData] = useState<paymentResponseData | null>(
    null,
  );
  const [loading, setLoading] = useState(true);
  const [bookingInfo, setBookingInfo] = useState<BookingInfo | null>(null);
  const hasRequested = useRef(false);
  useEffect(() => {
    if (hasRequested.current) return;
    hasRequested.current = true;

    async function confirmPayment() {
      setLoading(true);
      try {
        const orderId = searchParams.get("orderId");
        const paymentKey = searchParams.get("paymentKey");
        const amount = searchParams.get("amount");

        if (!orderId || !paymentKey) {
          throw new Error("param에 orderId와 paymentKey가 없습니다.");
        }

        const bookingData = getSessionData<BookingInfo>("bookingData");
        const userInfo = getSessionData<User>("userInfo");

        if (!bookingData || !userInfo) {
          throw new Error("session data에 결제 정보를 불러올 수 없습니다.");
        }
        setBookingInfo(bookingData);

        const requestData = {
          userId: userInfo.id,
          performanceScheduleId: bookingData.scheduleId,
          seatCount: bookingData.selectedSeats.length,
          seatIds: bookingData.selectedSeats.map((seat) => seat.seatId),
          orderId: orderId,
          amount: amount,
          paymentKey: paymentKey,
          bookingId: bookingData.bookingId
        };
        const response = await axios.post<paymentResponseData>(
          "/api/v1/payments/success",
          requestData,
        );
        setResponseData(response.data);
        console.log("결제 성공:", responseData);
        sessionStorage.removeItem("bookingData");
        sessionStorage.removeItem("userInfo");
        sessionStorage.removeItem("performanceId");
        sessionStorage.removeItem("scheduleId");
      } catch (error) {
        console.error("결제 오류:", error);
        const code = error instanceof Error ? error.name : "UnknownError";
        const message =
          error instanceof Error ? error.message : "알 수 없는 오류";

        navigate(`/fail?code=${code}&message=${message}`);
      } finally {
        setLoading(false);
      }
    }

    confirmPayment();
  }, [searchParams, navigate, axios]);

  return (
    <>
      {loading ? (
        <div>
          <p>결제 정보를 확인하고 있습니다.</p>
        </div>
      ) : (
        <div className={style.box_section} style={{ width: "600px" }}>
          <img
            width="100px"
            src="https://static.toss.im/illusts/check-blue-spot-ending-frame.png"
            alt="결제완료"
          />
          <h2>예매가 완료되었어요!</h2>
          <div
            className={`${style["p-grid"]} ${style["typography--p"]}`}
            style={{ marginTop: "10px" }}
          >
            <div className={`${style["p-grid-col"]} ${style["text--left"]}`}>
              <b>예매 번호</b>
            </div>
            <div
              className={`${style["p-grid-col"]} ${style["text--right"]}`}
              id="orderId"
            >
              {`${searchParams.get("orderId")}`}
            </div>
          </div>
          <div
            className={`${style["p-grid"]} ${style["typography--p"]}`}
            style={{ marginTop: "10px" }}
          >
            <div className={`${style["p-grid-col"]} ${style["text--left"]}`}>
              <b>예매 정보</b>
            </div>
            <div
              className={`${style["p-grid-col"]} ${style["text--right"]}`}
              id="bookingInfo"
            >
              {`${bookingInfo?.performance}`}
              <br />
              {`${bookingInfo?.theater}`}
            </div>
          </div>
          <div
            className={`${style["p-grid"]} ${style["typography--p"]}`}
            style={{ marginTop: "10px" }}
          >
            <div className={`${style["p-grid-col"]} ${style["text--left"]}`}>
              <b>공연 일정</b>
            </div>
            <div
              className={`${style["p-grid-col"]} ${style["text--right"]}`}
              id="performanceSchedule"
            >
              {`${bookingInfo?.date} ${bookingInfo?.time}`}
            </div>
          </div>
          <div
            className={`${style["p-grid"]} ${style["typography--p"]}`}
            style={{ marginTop: "50px" }}
          >
            <div className={`${style["p-grid-col"]} ${style["text--left"]}`}>
              <b>결제 금액</b>
            </div>
            <div
              className={`${style["p-grid-col"]} ${style["text--right"]}`}
              id="amount"
            >
              {`${Number(searchParams.get("amount")).toLocaleString()}원`}
            </div>
          </div>
          <div className={style["p-grid-col"]}>
            <Button onClick={() => navigate("/")}>
              메인 페이지로 
            </Button>
            <Button onClick={() => navigate(`../mypage/bookings/${responseData?.bookingId || ""}`)}>
              예매 내역 확인
            </Button>
            {/* <Link to="/">
              <button className="button p-grid-col5">메인 페이지로</button>
              {/* TODO: 공연 상세페이지로 이동 
            </Link> 
            <Link to={`../mypage/bookings/${responseData?.bookingId || ""}`}>
              <button
                className="button p-grid-col5"
                style={{ backgroundColor: "#e8f3ff", color: "#1b64da" }}
              >
                예매내역 확인
              </button>
            </Link>*/}
          </div>
        </div>
      )}
      {/* <div
        className={style.box_section}
        style={{ width: "600px", textAlign: "left" }}
      >
        <b>Response Data :</b>
        <div id="response" style={{ whiteSpace: "initial" }}>
          {responseData && <pre>{JSON.stringify(responseData, null, 4)}</pre>}
        </div>
      </div> */}
    </>
  );
}
