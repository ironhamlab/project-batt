import apiClient from "../axios";
import type { User } from "../../types/User";

export const fetchUserInfo = async (memberId: number): Promise<User> => {
  const { data } = await apiClient.get(`/api/v1/members?memberId=${memberId}`);
  return data;
};

export const withdrawMember = async (memberId: number): Promise<void> => {
  await apiClient.patch(`/api/v1/members/${memberId}`);
};
