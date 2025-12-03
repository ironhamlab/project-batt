import React from "react";
import type { ReactNode } from "react";
import styles from "./Modal.module.css";

interface ModalProps {
  isOpen: boolean;
  onClose?: () => void;
  children: ReactNode;
  showCloseButton?: boolean;
}

const Modal: React.FC<ModalProps> = ({
  isOpen,
  onClose,
  children,
  showCloseButton = false,
}) => {
  if (!isOpen) return null;

  const handleOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget && onClose) {
      onClose();
    }
  };

  return (
    <div className={styles.modalOverlay} onClick={handleOverlayClick}>
      <div className={styles.modalBox}>
        {showCloseButton && onClose && (
          <button className={styles.closeButton} onClick={onClose}>
            ×
          </button>
        )}
        {children}
      </div>
    </div>
  );
};

export default Modal;
