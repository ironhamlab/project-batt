import type { TransferPerformance } from "../../types/Performance";
import { Box, Typography } from "@mui/joy";
import TransferPerformanceItem from "./TransferPerformanceItem";
import { ConfirmationNumber } from "@mui/icons-material";

interface Props {
  performances: TransferPerformance[];
}

const TransferPerformanceList = ({ performances }: Props) => {
  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "1152px",
        mx: "auto",
        display: "flex",
        justifyContent: "center",
        p: 0,
        my: "20px",
      }}
    >
      {performances.length === 0 ? (
        <Box
          sx={{
            my: "100px",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <ConfirmationNumber
            sx={{
              fontSize: { xs: "80px", md: "150px" },
            }}
          />
          <Typography level={"body-lg"}>
            양도 진행 중인 공연 정보가 없습니다.
          </Typography>
        </Box>
      ) : (
        <Box
          sx={{
            width: "100%",
            mx: "auto",
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            gap: 0,
            p: 0,
          }}
        >
          {performances.map((performance) => (
            <TransferPerformanceItem
              key={performance.performanceId}
              performanceInfo={performance}
            />
          ))}
        </Box>
      )}
    </Box>
  );
};

export default TransferPerformanceList;
