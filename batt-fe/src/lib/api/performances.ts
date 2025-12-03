import apiClient from "../axios";
import type { PerformanceData } from "../../types/PerformanceData";
import type { PerformanceScheduleResponse } from "../../types/PerformanceSchedule";

export const getPerformanceDetail = async (
  performanceId: number,
): Promise<PerformanceData> => {
  const { data } = await apiClient.get(`/api/v1/performances/${performanceId}`);
  return data;
};

export const getPerformanceSchedule = async (
  performanceId: number,
): Promise<PerformanceScheduleResponse> => {
  const { data } = await apiClient.get(
    `/api/v1/performances/${performanceId}/schedule`,
  );
  return data;
};
