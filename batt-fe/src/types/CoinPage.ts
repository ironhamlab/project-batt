import type { Coin } from "./Coin";
import type { PageInfo } from "./ReviewPage";

export interface CoinPage {
  pageInfo: PageInfo;
  coins: Coin[];
}
