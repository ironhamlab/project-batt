import apiClient from "../axios";
import { useAuthStore } from "../../stores/authStore";

export const AccessTokenIssue = async () => {
  try {
    const response = await apiClient.post("/api/v1/token/reissue");
    const accessToken = response.headers["authorization"];
    if (!accessToken) {
      console.log("엑세스 토큰이 없습니다.");
      return false;
    }
    useAuthStore.getState().setAccessToken(accessToken);
    console.log("엑세스 토큰 저장 완료");
    useAuthStore.getState().setMemberId(response.data.memberId);
    useAuthStore.getState().setCoin(response.data.remainCoin);
    return true;
  } catch (error) {
    console.log("로그인 처리 중 오류 발생:", error);
    return false;
  }
};

// 백엔드에 로그아웃 요청을 보내 쿠키를 삭제하고,
// 성공 응답이 돌아오면 로컬의 엑세스 토큰을 삭제하는 함수.
export const DeleteToken = async () => {
  const response = await apiClient.post("/api/v1/members/logout");
  if (response.status === 200) {
    useAuthStore.getState().clearAuth();
    sessionStorage.removeItem("auth");
    console.log("로그아웃, 엑세스 토큰 삭제");
  }
};

// 헤더에 엑세스 토큰이 있으면 true, 없으면 false를 반환하는 함수.
export const getAccessToken = (): boolean => {
  const accessToken = useAuthStore.getState().accessToken;
  if (!accessToken) return false;

  return true;
};

// remainCoin 새로고침.
export const refreshRemainCoin = async () => {
  try {
    const response = await apiClient.get("/api/v1/coins/refresh");
    useAuthStore.getState().setCoin(response.data.remainCoin);
    return true;
  } catch (error) {
    console.log("batt 갱신 중 오류 발생:", error);
    return false;
  }
};
