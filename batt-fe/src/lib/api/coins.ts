import apiClient from "../axios";
import type { CoinPage } from "../../types/CoinPage";

interface FetchCoinsParams {
  memberId: number;
  page: number;
  size: number;
}

export const fetchCoins = async ({
  memberId,
  page,
  size,
}: FetchCoinsParams): Promise<CoinPage> => {
  const { data } = await apiClient.get(
    `/api/v1/coins?memberId=${memberId}&page=${page}&size=${size}`,
  );
  return data;
};
