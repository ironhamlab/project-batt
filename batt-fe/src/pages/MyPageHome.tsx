import MypageBox from "../components/mypage/MypageBox";
import styles from "../styles/MyPageHome.module.css";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import PaidOutlinedIcon from "@mui/icons-material/PaidOutlined";
import ConfirmationNumberOutlinedIcon from "@mui/icons-material/ConfirmationNumberOutlined";
import RateReviewOutlinedIcon from "@mui/icons-material/RateReviewOutlined";
import kakao from "../assets/images/kakao.png";
import naver from "../assets/images/naver.png";
import { useQuery } from "@tanstack/react-query";
import type { User } from "../types/User";
import { fetchUserInfo } from "../lib/api/members";
import { useAuthStore } from "../stores/authStore";
import { useNavigate } from "react-router-dom";
import { DeleteToken } from "../lib/api/auth";

const SettingsIcon = () => (
  <span>
    <SettingsOutlinedIcon />
  </span>
);
const TicketIcon = () => (
  <span>
    <ConfirmationNumberOutlinedIcon />
  </span>
);
const ReviewIcon = () => (
  <span>
    <RateReviewOutlinedIcon />
  </span>
);
const CoinIcon = () => (
  <span>
    <PaidOutlinedIcon />
  </span>
);

const MyPageHome = () => {
  const { memberId } = useAuthStore();
  const navigate = useNavigate();

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

  if (isLoading) {
    return <div className={styles.container}>로딩 중...</div>;
  }

  if (isError || !userInfo) {
    return (
      <div className={styles.container}>
        사용자 정보를 불러올 수 없습니다ㅜㅜ.
      </div>
    );
  }

  const currentUser = userInfo || memberId;

  if (!currentUser) {
    return (
      <div className={styles.container}>사용자 정보를 불러올 수 없습니다.</div>
    );
  }

  const isKakao = currentUser.providerType === "KAKAO";
  const providerIcon = isKakao ? kakao : naver;
  const providerName = isKakao ? "Kakao" : "Naver";

  const handleLogout = async () => {
    await DeleteToken();
    navigate("/", { replace: true });
  };

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>마이페이지</h1>

      <div className={styles.grid}>
        <MypageBox size="large">
          <div className={styles.welcomeContainer}>
            <div>
              <h2 className={styles.welcomeTitle}>
                {currentUser.name} 님 환영합니다
              </h2>
              <div className={styles.emailContainer}>
                <img
                  src={providerIcon}
                  alt={providerName}
                  className={styles.providerIcon}
                />
                <span className={styles.email}>{currentUser.email}</span>
              </div>
            </div>
            <button onClick={handleLogout} className={styles.logoutButton}>
              로그아웃
            </button>
          </div>
        </MypageBox>

        <MypageBox
          title="개인정보 관리"
          description="개인정보를 확인하고 관리합니다."
          linkTo="/mypage/info"
          icon={<SettingsIcon />}
        />

        <MypageBox
          title="예매 내역"
          description="구매한 티켓의 내역을 확인할 수 있습니다."
          linkTo="/mypage/bookings"
          icon={<TicketIcon />}
        />

        <MypageBox
          title="리뷰 관리"
          description="작성한 리뷰를 확인하고 수정할 수 있습니다."
          linkTo="/mypage/reviews"
          icon={<ReviewIcon />}
        />

        <MypageBox
          title="BATT 내역"
          description="자사 포인트인 BATT의 내역을 확인할 수 있습니다."
          linkTo="/mypage/coin"
          icon={<CoinIcon />}
        />
      </div>
    </div>
  );
};

export default MyPageHome;
