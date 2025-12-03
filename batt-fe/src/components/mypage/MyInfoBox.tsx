import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import styles from "@styles/MyInfoBox.module.css";
import modalStyles from "../common/Modal.module.css";
import naverIcon from "@/assets/images/naver.png";
import kakaoIcon from "@/assets/images/kakao.png";
import type { User } from "../../types/User";
import { fetchUserInfo, withdrawMember } from "../../lib/api/members";
import Modal from "../common/Modal";
import { useAuthStore } from "../../stores/authStore";
import Swal from "sweetalert2";
import { DeleteToken } from "../../lib/api/auth";

const MyInfoBox: React.FC = () => {
  const { memberId } = useAuthStore();
  const queryClient = useQueryClient();

  const {
    data: userInfo,
    isLoading,
    isError,
    error,
  } = useQuery<User, Error>({
    queryKey: ["userInfo", memberId],
    queryFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return fetchUserInfo(memberId);
    },
    enabled: !!memberId,
  });

  const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);
  const [isCompleteModalOpen, setIsCompleteModalOpen] = useState(false);
  const navigate = useNavigate();

  const withdrawMutation = useMutation({
    mutationFn: () => {
      if (!memberId) throw new Error("사용자 정보가 없습니다.");
      return withdrawMember(memberId);
    },
    onSuccess: () => {
      DeleteToken();
      queryClient.invalidateQueries({ queryKey: ["userInfo"] });
      setIsConfirmModalOpen(false);
      setIsCompleteModalOpen(true);
      setTimeout(() => {
        navigate("/");
      }, 2000);
    },
    onError: (error) => {
      console.error("회원 탈퇴 실패:", error);
      Swal.fire({
        icon: "error",
        text: "탈퇴 요청 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
      });
    },
  });

  if (isLoading) {
    return <div className={styles.box}>로딩 중...</div>;
  }

  if (isError) {
    return <div className={styles.box}>에러: {error?.message}</div>;
  }

  if (!userInfo) {
    return <div className={styles.box}>사용자 정보를 불러올 수 없습니다.</div>;
  }

  const providerDetails = {
    NAVER: { icon: naverIcon, name: "NAVER" },
    KAKAO: { icon: kakaoIcon, name: "KAKAO" },
  };

  const currentProvider =
    providerDetails[userInfo.providerType as keyof typeof providerDetails] ||
    providerDetails.NAVER;

  // const handleWithdrawClick = () => {
  //   setIsConfirmModalOpen(true);
  // };

  const handleConfirmWithdraw = () => {
    withdrawMutation.mutate();
  };

  const handleCancelWithdraw = () => {
    setIsConfirmModalOpen(false);
  };

  return (
    <div className={styles.box}>
      <div className={styles.detailsSection}>
        <div className={styles.infoGrid}>
          <span className={styles.label}>이름</span>
          <span className={styles.value}>{userInfo.name}</span>

          <span className={styles.label}>이메일</span>
          <span className={styles.value}>
            <img
              src={currentProvider.icon}
              alt={currentProvider.name}
              className={styles.providerIcon}
            />
            {userInfo.email}
          </span>

          <span className={styles.label}>연락처</span>
          <span className={styles.value}>{userInfo.phoneNumber}</span>

          <span className={styles.label}>생일</span>
          <span className={styles.value}>{userInfo.birth}</span>

          <span className={styles.label}>성별</span>
          <span className={styles.value}>{userInfo.sex}</span>
        </div>
        {/* <div className={styles.actions}>
          <button onClick={handleWithdrawClick} className={styles.withdrawLink}>
            BATT 탈퇴
          </button>
        </div> */}
      </div>

      <Modal isOpen={isConfirmModalOpen} onClose={handleCancelWithdraw}>
        <p className={modalStyles.modalText}>
          정말 떠나시나요?😢
          <br />
          일주일 동안 재가입이 불가능해요.
        </p>
        <div className={modalStyles.modalActions}>
          <button
            onClick={handleCancelWithdraw}
            className={modalStyles.cancelButton}
          >
            돌아가기
          </button>
          <button
            onClick={handleConfirmWithdraw}
            className={modalStyles.confirmButton}
          >
            정말 탈퇴하기
          </button>
        </div>
      </Modal>

      <Modal isOpen={isCompleteModalOpen}>
        <p className={modalStyles.modalText}>지금까지 감사했습니다.</p>
      </Modal>
    </div>
  );
};

export default MyInfoBox;
