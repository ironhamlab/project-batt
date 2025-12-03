import { useEffect } from "react";
import { Routes, Route, Outlet } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";

import MypageSidebar from "../components/mypage/MypageSidebar";
import styles from "../styles/MypageLayout.module.css";
import MyPageHome from "./MyPageHome";
import BookingDetailPage from "./BookingDetailPage";

import MyInfoBox from "../components/mypage/MyInfoBox";
import MyBookingBox from "../components/mypage/MyBookingBox";
import MyReviewBox from "../components/mypage/MyReviewBox";
import MyCoinBox from "../components/mypage/MyCoinBox";
import NotFoundPage from "./NotFoundPage";

const MyInfoPage = () => (
  <div className={styles.container}>
    <h1 className={styles.title}>개인정보 관리</h1>
    <MyInfoBox />
  </div>
);

const MyBookingPage = () => (
  <div className={styles.container}>
    <h1 className={styles.title}>예매 내역</h1>
    <MyBookingBox />
  </div>
);

const MyReviewPage = () => (
  <div className={styles.container}>
    <h1 className={styles.title}>리뷰 내역</h1>
    <MyReviewBox />
  </div>
);

const CoinPage = () => (
  <div className={styles.container}>
    <h1 className={styles.title}>BATT 내역</h1>
    <MyCoinBox />
  </div>
);

const MypageLayout = () => {
  const queryClient = useQueryClient();

  useEffect(() => {
    return () => {
      queryClient.removeQueries({ queryKey: ["userInfo"] });
      console.log("MyPage 캐시가 정리되었습니다.");
    };
  }, [queryClient]);

  return (
    <div className={styles.layout}>
      <MypageSidebar />
      <main className={styles.content}>
        <Routes>
          <Route index element={<MyPageHome />} />
          <Route path="info" element={<MyInfoPage />} />
          <Route path="bookings" element={<MyBookingPage />} />
          <Route path="bookings/:bookingId" element={<BookingDetailPage />} />
          <Route path="reviews" element={<MyReviewPage />} />
          <Route path="coin" element={<CoinPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
        <Outlet />
      </main>
    </div>
  );
};

export default MypageLayout;
