import {
  loadTossPayments,
  type TossPaymentsPayment,
} from "@tosspayments/tosspayments-sdk";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { createOrderId } from "../lib/api/booking";

const clientKey = "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq";
const customerKey = "K_ozFnAVJ0T65p9zudeA2";

export default function PaymentPage() {
  const location = useLocation();
  const { bookingData, userInfo } = location.state;
  console.log(location.state);
  const navigate = useNavigate();
  const [amount] = useState({
    currency: "KRW",
    value: bookingData.price ?? 0,
  });
  useEffect(() => {
    async function runPayment() {
      let orderId = "";
      try {
        orderId = await createOrderId();
        console.log("생성된 주문 ID:", orderId);
      } catch (err) {
        console.error("주문 ID 생성 실패:", err);
        return;
      }
      const tossPayments = await loadTossPayments(clientKey);
      try {
        console.log("결제창 불러오기");
        const payment = tossPayments.payment({
          customerKey: customerKey,
        }) as TossPaymentsPayment;
        await payment.requestPayment({
          method: "CARD",
          amount: amount,
          orderId: orderId,
          orderName: bookingData.performance,
          successUrl: window.location.origin + "/success",
          failUrl: window.location.origin + "/fail",
          customerEmail: userInfo.email,
          customerName: userInfo.name,
          customerMobilePhone: userInfo.phone,
          card: {
            useEscrow: false,
            flowMode: "DEFAULT",
            useCardPoint: false,
            useAppCardOnly: false,
          },
        });
      } catch (err) {
        const error = err as { code?: string; message?: string };
        if (error?.code === "USER_CANCEL") {
          console.log("사용자가 결제를 취소했습니다.");
          navigate(`/booking`, {replace: true});
        }
      }
    }
    sessionStorage.setItem("bookingData", JSON.stringify(bookingData));
    sessionStorage.setItem("userInfo", JSON.stringify(userInfo));
    runPayment();
  }, []);
  useEffect(() => {
    const handlePopState = () => {
      const confirmCancle = window.confirm("결제를 취소하시겠습니까?")
      if (confirmCancle) {
        navigate(`/booking`, { replace: true });
      } else {
        window.history.pushState(null, "", window.location.href);
      }
    }
    window.addEventListener("popstate", handlePopState);
    return () => window.removeEventListener("popstate", handlePopState);
  }, [navigate])
  return (
    <div>
      <p>결제 진행 중</p>
    </div>
  );
}
