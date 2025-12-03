import type { User } from "../../../../types/User";
import BookingInfoForm from "../molecules/BookingInfoForm";
import TsAndCs from "../molecules/TsAndCs";
interface Props {
  userInfo: User;
  agreed: boolean;
  setUserInfo: (field: string, value: string | boolean) => void;
}
export default function BookinguserForm({ userInfo, setUserInfo }: Props) {
  return (
    <div
      style={{
        overflowY: "auto",
      }}
    >
      <div
        style={{
          overflowY: "auto",
        }}
      >
        <BookingInfoForm userInfo={userInfo} setUserInfo={setUserInfo} />
        <TsAndCs setUserInfo={setUserInfo} />
      </div>
      <div></div>
    </div>
  );
}
