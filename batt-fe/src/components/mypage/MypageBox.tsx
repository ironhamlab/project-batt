import React from "react";
import { Link } from "react-router-dom";
import styles from "@styles/MypageBox.module.css";

export interface MypageBoxProps {
  size?: "small" | "large";
  title?: string;
  description?: string;
  linkTo?: string;
  icon?: React.ReactNode;
  children?: React.ReactNode;
  className?: string;
}

const MypageBox: React.FC<MypageBoxProps> = ({
  size = "small",
  title,
  description,
  linkTo,
  icon,
  children,
  className = "",
}) => {
  const boxClasses = `${styles.box} ${
    size === "large" ? styles.large : ""
  } ${className}`;

  if (children) {
    return <div className={boxClasses}>{children}</div>;
  }

  return (
    <div className={boxClasses}>
      <div className={styles.textContainer}>
        {title && <h3 className={styles.title}>{title}</h3>}
        {description && <p className={styles.description}>{description}</p>}
      </div>
      <div className={styles.bottomContainer}>
        {linkTo ? (
          <Link to={linkTo} className={styles.link}>
            바로가기 &gt;
          </Link>
        ) : (
          <div />
        )}
        {icon && <div className={styles.iconWrapper}>{icon}</div>}
      </div>
    </div>
  );
};

export default MypageBox;
