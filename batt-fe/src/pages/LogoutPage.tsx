import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { DeleteToken } from "../lib/api/auth";
// import apiClient from "../lib/axios";

const LogoutPage = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const handleLogout = async () => {
      // try {
      //   // 백엔드에 로그아웃 요청.
      //   // 리프레시 토큰은 백에서 삭제.
      //   // await apiClient.post();
      //   console.log("백엔드에 로그아웃 요청 필요함")
      // } catch (error) {
      //   console.log("로그아웃 실패:", error);
      // } finally {
      //  DeleteToken();

      await DeleteToken();
      navigate("/");
      // };
    };
    handleLogout();
  }, []);

  return <div>로그아웃 페이지</div>;
};

export default LogoutPage;
