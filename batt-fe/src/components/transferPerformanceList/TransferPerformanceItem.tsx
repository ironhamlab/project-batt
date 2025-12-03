import React from "react";
import type { TransferPerformance } from "../../types/Performance";
import { Box, Typography, Button } from "@mui/joy";
import { useNavigate } from "react-router-dom";

interface Props {
  performanceInfo: TransferPerformance;
}

const TransferPerformanceItem: React.FC<Props> = ({ performanceInfo }) => {
  const navigate = useNavigate();

  const performanceStartDate = new Date(performanceInfo.performanceStartDate);
  const performanceEndDate = new Date(performanceInfo.performanceEndDate);
  const pad = (n: number) => String(n).padStart(2, "0");
  const formattedStartDate = `${pad(performanceStartDate.getFullYear())}.${pad(performanceStartDate.getMonth() + 1)}.${pad(performanceStartDate.getDate())}`;
  const formattedEndDate = `${pad(performanceEndDate.getFullYear())}.${pad(performanceEndDate.getMonth() + 1)}.${pad(performanceEndDate.getDate())}`;

  const dateRange =
    formattedStartDate === formattedEndDate
      ? formattedStartDate
      : `${formattedStartDate} - ${formattedEndDate}`;

  const handleClick = () => {
    navigate(`/transfer-performance/${performanceInfo.performanceId}`);
  };

  return (
    <Box
      sx={{
        width: "100%",
        height: "180px",
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        p: "10px",
        flexShrink: 0,
        borderBottom: "1px solid #c1c1c1ff",
      }}
    >
      <Box
        sx={{
          height: "100%",
          width: "112px",
          mr: "10px",
          p: 0,
          bgcolor: "#FCFCFC",
          "& img": {
            height: "100%",
            borderRadius: "4px",
            objectFit: "cover",
          },
        }}
      >
        <img
          src={performanceInfo.posterImageUrl}
          alt={`${performanceInfo.title} 포스터`}
        />
      </Box>
      <Box
        sx={{
          height: "100%",
          width: "70%",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          gap: "10px",
        }}
      >
        <Typography
          sx={{
            overflow: "hidden",
            whiteSpace: "normal",
            textOverflow: "ellipsis",
            display: "-webkit-box",
            WebkitLineClamp: "2",
            WebkitBoxOrient: "vertical",
            wordBreak: "break-word",
          }}
          level="h4"
        >
          {performanceInfo.title}
        </Typography>
        <Typography
          sx={{
            overflow: "hidden",
            whiteSpace: "nowrap",
            textOverflow: "ellipsis",
          }}
          level="title-md"
        >
          멀티 아트센터 역삼
        </Typography>
        <Typography level="body-md">{dateRange}</Typography>
      </Box>

      {/* 버튼 */}
      <Button
        sx={{
          width: "140px",
          height: "50px",
          fontSize: "17px",
          bgcolor: "#4A4A8C",
        }}
        variant="solid"
        onClick={handleClick}
      >
        양도 티켓{" "}
        <span style={{ color: "#FFC300", margin: "0 4px" }}>
          {" "}
          {performanceInfo.transferTicketCount}{" "}
        </span>
        매
      </Button>
    </Box>
  );
};

export default TransferPerformanceItem;
