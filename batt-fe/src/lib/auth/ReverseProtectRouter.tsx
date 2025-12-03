import { Navigate } from "react-router-dom";
import type { JSX } from "react";
import { getAccessToken } from "../api/auth";

interface ReverseProtectRouteProps {
  children: JSX.Element;
}

export default function ReverseProtectRoute({
  children,
}: ReverseProtectRouteProps) {
  const isAuthenticated = getAccessToken();

  if (isAuthenticated) {
    window.alert("잘못된 접근입니다.");
    return <Navigate to="/" replace />;
  }

  return children;
}
