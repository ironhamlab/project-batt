export interface PerformanceTimeInfo {
  performanceScheduleId: number;
  performanceTime: string;
}

export interface PerformanceSchedule {
  performanceDate: string;
  performanceTimeInfo: PerformanceTimeInfo[];
}

export interface PerformanceScheduleResponse {
  performanceId: number;
  performanceSchedule: PerformanceSchedule[];
}
