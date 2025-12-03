export interface Coin {
  id: number;
  amount: number;
  description: string;
  status:
    | "SIGN_UP"
    | "REVIEW"
    | "TRANSFER_SUCCESS"
    | "TRANSFER_FAIL"
    | "BID_SUCCESS"
    | "BID_FAIL";
  remainCoin: number;
  logCreatedAt: number;
}
