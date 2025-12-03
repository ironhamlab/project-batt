import { Navigate } from "react-router-dom";
import type { JSX } from "react";
import { getAccessToken } from "../api/auth";

interface ProtectRouteProps {
  children: JSX.Element;
}

export default function ProtectRoute({ children }: ProtectRouteProps) {
  const isAuthenticated = getAccessToken();

  if (!isAuthenticated) {
    localStorage.setItem(
      "redirectPath",
      window.location.pathname + window.location.search,
    );
    window.alert("로그인 후 사용할 수 있습니다.");
    return <Navigate to="/login" replace />;
  }

  return children;
}
