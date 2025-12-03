import Header from "../components/booking/Header.tsx";
import { useEffect, useRef, useState } from "react";
import ChoosingSeats from "../components/booking/ChoosingSeats/organism/ChoosingSeats.tsx";
import type { BookingInfo } from "../types/BookingInfo.ts";
import Confirmation from "../components/booking/CheckingInfo/organims/Confirmation.tsx";
import { useBlocker, useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { getSeats, holdSeats, releaseSeats } from "../lib/api/booking.ts";
import type { SeatCoordinate } from "../types/SeatCoordinate.ts";
// import { isAxiosError } from "axios";
import { getPerformanceDetail } from "../lib/api/performances.ts";
import { useQuery } from "@tanstack/react-query";
import { useAuthStore } from "../stores/authStore.ts";
import { fetchUserInfo } from "../lib/api/members.ts";
import type { User } from "../types/User.ts";
import TicketLoader from "../components/common/TicetLoader.tsx";
import type { Seat } from "../types/Seat.ts";

export default function BookingPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const baseURL = import.meta.env.VITE_API_URL || "http://localhost:8080";
  const scheduleId = Number(sessionStorage.getItem("scheduleId"));
  const performanceId = Number(sessionStorage.getItem("performanceId"));
  const performanceDate = sessionStorage.getItem("performanceDate");
  const performanceTime = sessionStorage.getItem("performanceTime");
  const [searchParams] = useSearchParams();
  const step = searchParams.get("step") || "seat";
  const bookingInfo: BookingInfo = {
    bookingId: null,
    selectedSeats: [],
    date: "2023-10-01",
    time: "19:00",
    price: 0,
    performance: "뮤지컬 멤피스",
    theater: "예술의 전당",
    scheduleId: scheduleId,
  };
  const [shoudBlock, setShouldBlock] = useState(true);
  const [info, setInfo] = useState<BookingInfo>(bookingInfo);
  const [seats, setSeats] = useState<SeatCoordinate[]>([]);
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [holdExpiredTime, setExpiredTime] = useState<Date | null>(null);
  const [timeLeft, setTimeLeft] = useState<number>(600000);
  const [posterUrl, setPoseterUrl] = useState<string>("");
  const { memberId } = useAuthStore();
  const {
    data: userInfo,
    isLoading,
    isError,
  } = useQuery<User, Error>({
    queryKey: ["userInfo", memberId],
    queryFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return fetchUserInfo(memberId);
    },
    enabled: !!memberId,
  });
  useEffect(() => {
    if (step === "confirm") {
      window.location.replace("/booking");
    }
  }, [])
  useEffect(() => {
    if (isError) {
      console.error("사용자 정보를 불러오는 데 실패했습니다.");
      alert("사용자 정보를 불러오는 데 실패했습니다. 다시 시도해주세요.");
      navigate("/");
    }
  }, [isError, navigate]);
  useEffect(() => {
    if (userInfo) {
      setUser(userInfo);
    }
    setLoading(isLoading);
  }, [userInfo, isLoading]);

  useEffect(() => {
    if (!performanceId || !scheduleId || !performanceDate || !performanceTime) {
      alert("공연 정보를 불러올 수 없습니다.");
      navigate("/");
      return;
    }

    const getSeatData = async function () {
      try {
        const seats = await getSeats(scheduleId);
        setSeats(seats);
      } catch (err) {
        console.log(`실패:${err}`);
      }
    };

    const getPerformanceData = async function () {
      try {
        const performance = await getPerformanceDetail(performanceId);
        setInfo((prev) => ({
          ...prev,
          performance: performance.title,
          theater: performance.venueName,
          date: performanceDate,
          time: performanceTime
        }));
        setPoseterUrl(performance.posterImageUrl);
      } catch (err) {
        console.log(`공연 정보를 불러오는 데 실패했습니다: ${err}`);
      }
    };
    getPerformanceData();
    getSeatData();
  }, [performanceId, scheduleId, performanceDate, performanceTime]);

  const navigateInternal = (path: string, options?: any) => {
    setShouldBlock(false);
    navigate(path, options)

    setTimeout(() => {
      setShouldBlock(true);
    }, 3000)
  }

  const handleGoBack = function () {
    navigate(`/performance/${performanceId}`, {replace: true});
  }

  const handleHoldSeats = async () => {

    const result = await holdSeats(
      scheduleId,
      memberId,
      info.selectedSeats.map((s) => s.seatId),
    );
    console.log("좌석 선점 결과:", result);
    if (result.success) {
      setShouldBlock(false);
      sessionStorage.setItem("selectedSeats", JSON.stringify(info.selectedSeats))
      console.log("sessionStorage selectedSeats:", sessionStorage.getItem("selectedSeats"));
      navigate("/booking?step=confirm");
      setExpiredTime(new Date(Number(result.data?.holdExpiresAt) * 1000));
      setTimeout(() => {
        setShouldBlock(true);
      }, 3000)
    } else if (result.code === "BATT703") {
      alert("이미 선택된 좌석입니다.");
      window.location.reload();
    }
  };
  const handleReleaseSeats = async () => {
    const savedSeatsRaw = sessionStorage.getItem("selectedSeats");
    const savedSeats: Seat[] = savedSeatsRaw ? JSON.parse(savedSeatsRaw) : [];
    console.log(savedSeats)
    if (!savedSeats || savedSeats.length === 0) {
      console.log("해제할 좌석 없음:" + savedSeats)
      return
    }
    try {
      await releaseSeats(
        scheduleId,
        memberId,
        savedSeats.map((s) => s.seatId),
      )
      console.log(savedSeats)
    } catch (error) {
      console.error("좌석 해제 중 오류 발생:", error);
    }
  }
  useEffect(() => {
    if (!holdExpiredTime) return;
    const updateTime = setInterval(() => {
      const now = Date.now();
      const diff = holdExpiredTime.getTime() - now;
      if (diff <= 0) {
        setTimeLeft(0);
        clearInterval(updateTime);
        handleExpireSeats();
      } else {
        setTimeLeft(diff);
      }
    }, 1000);

    return () => clearInterval(updateTime);
  }, [holdExpiredTime]);

  const handleExpireSeats = () => {
    alert("결제 가능 시간이 초과되었습니다. 예매 초기 화면으로 돌아갑니다.");
    navigate(`/performance/${performanceId}`, { replace: true });
    // sessionStorage.removeItem("scheduleId");
    // sessionStorage.removeItem("performanceId");
  };
  const goToPayment = function () {
    if (!user) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    setShouldBlock(false);
    navigateInternal("/payment", { state: { bookingData: info, userInfo: user } });
  };

  const isHandlingBlockRef = useRef(false);
  const previousStepRef = useRef<string>(searchParams.get("step") || "seat");


  useEffect(() => {
    const handleBeforeUnload = async (event: BeforeUnloadEvent) => {
      if (shoudBlock && !isHandlingBlockRef.current) {
        // 동기 호출이어야 브라우저가 닫히기 전에 실행됨
        // 근데 굳이 동기 호출이어야 할까?
        //동기는 navigator.sendBeacon()을 사용해야 함
        isHandlingBlockRef.current = true;
        event.preventDefault();
        const confirmMessage = "선점한 좌석을 해제하고 페이지를 나가시겠습니까?";
        event.returnValue = confirmMessage; // Chrome에서 경고 메시지를 표시하기 위해 필요함
        const savedSeatsRaw = sessionStorage.getItem("selectedSeats");
        const savedSeats: Seat[] = savedSeatsRaw ? JSON.parse(savedSeatsRaw) : [];
        console.log("releaseSeats 호출 params", { scheduleId, memberId, seatIds: savedSeats.map(s => s.seatId) });
        const payload = JSON.stringify({ seatIds: savedSeats.map(s => s.seatId) });
        const blob = new Blob([payload], { type: "application/json" });

        navigator.sendBeacon(
          `${baseURL}/api/v1/performance-schedules/${scheduleId}/seats/release/${memberId}`,
          blob
        );

        console.log("좌석 해제");
        console.log(info.selectedSeats)
        isHandlingBlockRef.current = false;
        return confirmMessage
      }
    };

    window.addEventListener("beforeunload", handleBeforeUnload)
    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload)
    }
  }, [location.pathname]);

  // 경로 변경 감지 (react-router useBlocker)
  const blocker = useBlocker(({ nextLocation }) => {
    if (nextLocation.pathname === "/payment") return false;
    return step === "confirm";
  });

  useEffect(() => {
    const handlePopState = () => {
      isHandlingBlockRef.current = true;
      window.location.replace("/booking");
      blocker.reset?.(); // Blocker 재실행 방지
      isHandlingBlockRef.current = false;
    }
    const currentStep = new URLSearchParams(window.location.search).get("step") || "seat";
    if (
      previousStepRef.current === "confirm" &&
      currentStep === "seat" &&
      !isHandlingBlockRef.current
    ) {
      previousStepRef.current = currentStep;
    };

    window.addEventListener("popstate", handlePopState);
    return () => {
      window.removeEventListener("popstate", handlePopState);
    };
  }, [blocker]);


  useEffect(() => {
    if (blocker.state === "blocked" && !isHandlingBlockRef.current) {
      isHandlingBlockRef.current = true;

      const confirmLeave = window.confirm("선점한 좌석을 해제하고 페이지를 나가시겠습니까?");
      if (confirmLeave) {
        (async () => {
          try {
            await handleReleaseSeats(); // 해제 완료까지 대기
            console.log("좌석 해제 (route change)");

            if (blocker.state === "blocked") {
              blocker.proceed(); // 해제 끝난 후 페이지 이동
            }
          } finally {
            isHandlingBlockRef.current = false;
          }
        })();
      } else {
        blocker.reset();
        isHandlingBlockRef.current = false;
      }
    }
  }, [blocker.state, handleReleaseSeats]);



  if (loading) {
    return <TicketLoader />;
  }
  return (

    <div

      style={{
        height: "100vh",
      }}
    >
      <Header
        title={info.performance}
        venue={info.theater}
        posterUrl={posterUrl}
        date={info.date}
        time={info.time}
      />
      <div
        style={{
          flex: 1,
        }}
      >
        {step === "seat" && (
          <ChoosingSeats
            seats={seats}
            selectedSeats={info.selectedSeats}
            setInfo={setInfo}
            onClick={handleHoldSeats}
            handlePrevBtn={handleGoBack}
          />
        )}
        {step === "confirm" && user && (
          <Confirmation
            bookingInfo={info}
            handleNext={goToPayment}
            user={user}
            leftTime={timeLeft}
          />
        )}
      </div>
    </div>
  );
}
