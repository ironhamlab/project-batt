import axios from "axios";
import { useAuthStore } from "../stores/authStore";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  withCredentials: true,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers["Authorization"] = token;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const response = await apiClient.post("/api/v1/token/reissue");
        const newAccessToken = response.headers["authorization"];
        if (newAccessToken) {
          useAuthStore.getState().setAccessToken(newAccessToken);
          originalRequest.headers["Authorization"] = newAccessToken;
          return apiClient(originalRequest);
        }
      } catch (refreshError) {
        console.log("토큰 갱신 실패:", refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default apiClient;
