import { useParams } from "react-router-dom";
import BookingDetailBox from "../components/mypage/BookingDetailBox";
import styles from "../styles/BookingDetailPage.module.css";

const BookingDetailPage = () => {
  const { bookingId } = useParams<{ bookingId: string }>();

  if (!bookingId) {
    return <div>유효하지 않은 접근입니다.</div>;
  }

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>예매 내역 상세</h1>
      <BookingDetailBox bookingId={Number(bookingId)} />
    </div>
  );
};

export default BookingDetailPage;
