import React from "react";
import styles from "../styles/Footer.module.css";

const Footer: React.FC = () => {
  return (
    <footer className={styles.footer}>
      <div className={styles.container}>
        <div className={styles.brand}>BATT</div>
        <div className={styles.infoGrid}>
          <div className={styles.label}></div>
          <div className={styles.value}>
            서울특별시 강남구 테헤란로 212 (역삼동) 멀티캠퍼스 역삼
          </div>

          <div className={styles.label}></div>
          <div className={styles.value}>A506 No-Conflict</div>

          <div className={styles.label}></div>
          <div className={styles.value}>&copy; SSAFY</div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
