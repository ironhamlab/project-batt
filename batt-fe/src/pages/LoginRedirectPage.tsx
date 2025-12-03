import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AccessTokenIssue } from "../lib/api/auth";

const LoginRedirectPage = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const handleLogin = async () => {
      const success = await AccessTokenIssue();
      if (success) {
        const redirectPath = localStorage.getItem("redirectPath") || "/";
        navigate(redirectPath, { replace: true });
      } else {
        window.alert("로그인 중 문제가 발생했습니다. 다시 시도해 주세요.");
        navigate("/login", { replace: true });
      }
    };
    handleLogin();
  }, [navigate]);
  return <div>로그인 처리 중...</div>;
};

export default LoginRedirectPage;
