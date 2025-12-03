import { FormControl, FormHelperText, FormLabel, Input } from "@mui/joy";

import type { User } from "../../../../types/User";
import { PhoneMaskAdapter } from "../atom/PhoneMaskAdapter";

interface UserProps {
  userInfo: User;
  setUserInfo: (field: keyof User, value: string) => void;
}
// 나중에 고쳐야 하세요
// interface Props {
//   name: string
//   onChange: (event: { target: { name: string; value: string } }) => void;
// }

// const PhoneMaskAdapter = React.forwardRef<
//   HTMLInputElement,
//   {
//     name: string;
//     onChange: (event: { target: { name: string; value: string } }) => void;
//   }
// >(function PhoneMaskAdapter(props, ref) {
//   const { onChange, ...other } = props;
//   return (
//     <IMaskInput
//       {...other}
//       mask="000-0000-0000"
//       definitions={{
//         "0": /[0-9]/,
//       }}
//       inputRef={ref}
//       onAccept={(value) =>
//         onChange({ target: { name: props.name, value } })
//       }
//       overwrite
//     />
//   );
// });
export default function BookingInfoForm({ userInfo, setUserInfo }: UserProps) {
  return (
    <div
      style={{
        padding: "20px 10px",
        borderBottom: "solid 1px #ccc",
      }}
    >
      <h2>예매자 정보</h2>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "10px",
        }}
      >
        <FormControl>
          <FormLabel>이름</FormLabel>
          <Input name="name" value={userInfo.name} disabled />
        </FormControl>
        <FormControl>
          <FormLabel>생년월일</FormLabel>
          <Input name="birthday" value={userInfo.birth} disabled />
        </FormControl>
        <FormControl>
          <FormLabel>전화번호</FormLabel>
          <Input
            name="phone"
            value={userInfo.phoneNumber}
            onChange={(event) => setUserInfo("phoneNumber", event.target.value)}
            slotProps={{
              input: {
                component: PhoneMaskAdapter,
              },
            }}
          />
        </FormControl>
        <FormControl>
          <FormLabel>이메일</FormLabel>
          <Input
            name="email"
            value={userInfo.email}
            onChange={(event) => setUserInfo("email", event.target.value)}
          />
        </FormControl>
        <FormHelperText>
          본인 확인을 위해 정확한 정보를 입력해주세요.
        </FormHelperText>
      </div>
    </div>
  );
}
