import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

interface AuthState {
  accessToken: string;
  memberId: number;
  coin: number;
  setAccessToken: (token: string) => void;
  setMemberId: (memberId: number) => void;
  setCoin: (coin: number) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: "",
      memberId: 0,
      coin: 0,
      setAccessToken: (token: string) => set({ accessToken: token }),
      setMemberId: (memberId: number) => set({ memberId }),
      setCoin: (coin: number) => set({ coin }),
      clearAuth: () => {
        set({ accessToken: "", memberId: 0, coin: 0 });
      },
    }),
    {
      name: "auth",
      storage: createJSONStorage(() => sessionStorage),
    },
  ),
);
