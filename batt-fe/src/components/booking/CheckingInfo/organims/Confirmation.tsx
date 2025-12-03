import { Divider, Grid, useTheme } from "@mui/joy";
import type { BookingInfo } from "../../../../types/BookingInfo";
import TicketInfo from "../molecules/TicketInfo";
import BookinguserForm from "./BookingUserForm";
import { useState } from "react";
import type { User } from "../../../../types/User";
import { useMediaQuery } from "@mui/material";

interface Props {
  user: User;
  bookingInfo: BookingInfo;
  handleNext: () => void;
  leftTime: number;
}

export default function Confirmation({
  user,
  bookingInfo,
  handleNext,
  leftTime,
}: Props) {
  const [userInfo, setUserInfo] = useState({
    user: user,
    agreed: false,
  });

  const isAllChecked =
    userInfo.user.name !== "" &&
    userInfo.user.birth !== "" &&
    userInfo.user.phoneNumber !== "" &&
    userInfo.user.email !== "" &&
    userInfo.agreed;

  const theme = useTheme();
  const isMdUp = useMediaQuery(theme.breakpoints.up("md"));

  return (
    <div
      style={{
        // overflowY: isMdUp ? "auto" : "hidden"
      }}
    >
      <Grid container spacing={0.5}
        sx={{
          height: "100%",
          minHeight: 0,

        }}>
        <Grid
          xs={12}
          md={8}
          sx={{
            padding: "50px",
            overflowY: "auto",
          }}
        >
          <BookinguserForm
            userInfo={userInfo.user}
            setUserInfo={(field, value) => {
              setUserInfo((prev) => ({
                ...prev,
                [field]: value,
                user: {
                  ...prev.user,
                  [field]: value,
                },
              }));
            }}
            agreed={userInfo.agreed}
          />
        <Divider orientation={isMdUp ? "vertical" : "horizontal"} />
        </Grid>
        <Grid
          xs={12}
          md={4}
          sx={{
            borderLeft: "1px solid #ccc",
            padding: "50px",
            position: "sticky",
            top: 0,
            height: "100%",
          }}
        >
          <TicketInfo
            bookingInfo={bookingInfo}
            isAllChecked={isAllChecked}
            handleNext={handleNext}
            leftTime={leftTime}
          />
        </Grid>
      </Grid>

    </div>
  );
}
