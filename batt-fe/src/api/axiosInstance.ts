import axios from "axios";

const token = "00000000"; // 수정 필요
const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  },
});
export default axiosInstance;
