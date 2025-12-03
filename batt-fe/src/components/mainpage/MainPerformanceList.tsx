import { Box, Typography } from "@mui/joy";
import React from "react";
import MainPerformanceCard from "./MainPerformanceCard";
import type { Performance } from "../../types/Performance";
import { SentimentDissatisfied } from "@mui/icons-material";

interface OwnProp {
  performances: Performance[];
}

const MainPerformanceList: React.FC<OwnProp> = ({ performances }) => {
  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "1152px",
        display: "flex",
        flexDirection: "row",
        justifyContent: "center",
        flexWrap: "wrap",
        p: 0,
        mx: "auto",
        my: "20px",
      }}
    >
      {performances.length === 0 ? (
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            my: "30px",
          }}
        >
          <SentimentDissatisfied
            sx={{
              fontSize: { xs: "80px", md: "150px" },
            }}
          />
          <Typography level={"body-lg"}>공연 정보가 없습니다.</Typography>
        </Box>
      ) : (
        <Box
          sx={{
            width: "100%",
            display: "flex",
            flexDirection: "row",
            justifyContent: "flex-start",
            flexWrap: "wrap",
            gap: "37.3px",
            mx: "auto",
          }}
        >
          {performances.map((performance) => (
            <MainPerformanceCard
              key={performance.id}
              performanceInfo={performance}
            />
          ))}
        </Box>
      )}
    </Box>
  );
};

export default MainPerformanceList;
