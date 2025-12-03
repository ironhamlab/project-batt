import React from "react";
import Card from "@mui/joy/Card";
import CardContent from "@mui/joy/CardContent";
import Typography from "@mui/joy/Typography";
import AspectRatio from "@mui/joy/AspectRatio";
import type { Performance } from "../../types/Performance";
import Box from "@mui/joy/Box";
import { CardActionArea } from "@mui/material";
import { Link } from "react-router-dom";

interface OwnProp {
  performanceInfo: Performance;
}

const MainPerformanceCard: React.FC<OwnProp> = ({ performanceInfo }) => {
  const performanceStartDate = new Date(performanceInfo.performanceStartDate);
  const performanceEndDate = new Date(performanceInfo.performanceEndDate);
  const bookingOpenDate = new Date(performanceInfo.bookingOpenDate);
  const pad = (n: number) => String(n).padStart(2, "0");
  const formattedStartDate = `${pad(performanceStartDate.getFullYear())}.${pad(performanceStartDate.getMonth() + 1)}.${pad(performanceStartDate.getDate())}`;
  const formattedEndDate = `${pad(performanceEndDate.getFullYear())}.${pad(performanceEndDate.getMonth() + 1)}.${pad(performanceEndDate.getDate())}`;
  const formattedBookingOpenDate = `${pad(bookingOpenDate.getFullYear())}.${pad(bookingOpenDate.getMonth() + 1)}.${pad(bookingOpenDate.getDate())} ${pad(bookingOpenDate.getHours())}:${pad(bookingOpenDate.getMinutes())}`;
  const dateRange =
    formattedStartDate === formattedEndDate
      ? formattedStartDate
      : `${formattedStartDate} - ${formattedEndDate}`;

  return (
    <Card
      sx={{
        width: "260px",
        height: "513px",
        m: 0,
        p: 1,
        flexShrink: 0,
        background: "transparent",
        pointerEvents: performanceInfo.status === "SCHEDULE" ? "none" : "auto",
      }}
      variant="plain"
    >
      <CardActionArea
        component={Link}
        to={`/performance/${performanceInfo.id}`}
      >
        <AspectRatio ratio="3/4">
          <img
            src={performanceInfo.posterImageUrl}
            alt={`${performanceInfo.title} 포스터`}
          />
          {performanceInfo.status === "SCHEDULE" && (
            <Box
              sx={{
                position: "absolute",
                top: 0,
                left: 0,
                width: "100%",
                height: "100%",
                bgcolor: "rgba(0,0,0,0.4)", // 반투명 검은색
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Typography
                sx={{
                  color: "white",
                  position: "absolute",
                  fontSize: "1.5rem",
                  fontWeight: "bold",
                  textAlign: "center",
                }}
              >
                {formattedBookingOpenDate}
                <br />
                오픈예정
              </Typography>
            </Box>
          )}
        </AspectRatio>
        <CardContent
          sx={{
            width: "100%",
            gap: "10px",
          }}
        >
          <Typography
            sx={{
              overflow: "hidden",
              lineHeight: "150%",
              maxHeight: "50px",
              whiteSpace: "normal",
              textOverflow: "ellipsis",
              display: "-webkit-box",
              WebkitLineClamp: "2",
              WebkitBoxOrient: "vertical",
              wordBreak: "break-word",
            }}
            level="title-lg"
          >
            {performanceInfo.title}
          </Typography>
          <Typography
            sx={{
              overflow: "hidden",
              whiteSpace: "nowrap",
              textOverflow: "ellipsis",
            }}
            level="body-md"
          >
            멀티 아트센터 역삼
          </Typography>
          <Typography level="body-md">{dateRange}</Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

export default MainPerformanceCard;
