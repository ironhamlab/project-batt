import { Link, useLocation } from "react-router-dom";
import styles from "@styles/MypageSidebar.module.css";

const MypageSidebar = () => {
  const location = useLocation();

  const menuItems = [
    { path: "/mypage", label: "마이페이지 홈", isTitle: true },
    { path: "/mypage/info", label: "개인정보 관리", isTitle: false },
    { path: "/mypage/bookings", label: "예매 내역", isTitle: false },
    { path: "/mypage/reviews", label: "리뷰 내역", isTitle: false },
    { path: "/mypage/coin", label: "BATT 내역", isTitle: false },
  ];

  return (
    <aside className={styles.sidebar}>
      <div className={styles.logoContainer}>
        <Link to="/">
          <img src="/images/Logo.png" alt="BATT 로고" className={styles.logo} />
        </Link>
      </div>
      <nav className={styles.nav}>
        <ul>
          {menuItems.map((item) => (
            <li
              key={item.path}
              className={`${styles.menuItem} ${location.pathname === item.path ? styles.active : ""}`}
            >
              <Link to={item.path}>{item.label}</Link>
            </li>
          ))}
        </ul>
      </nav>
      <div className={styles.exit}>
        <Link to="/">메인으로</Link>
      </div>
    </aside>
  );
};

export default MypageSidebar;
