import { Box, Button, Typography } from "@mui/joy";
import MainPerformanceCard from "../mainpage/MainPerformanceCard";
import type { Performance } from "../../types/Performance";
import { useNavigate } from "react-router-dom";
import { SearchOff } from "@mui/icons-material";

interface OwnProp {
  performances: Performance[];
}

const ResultList: React.FC<OwnProp> = ({ performances }) => {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "1152px",
      }}
    >
      {performances.length > 0 ? (
        <Box
          sx={{
            width: "100%",
            display: "flex",
            flexDirection: "row",
            justifyContent: "flex-start",
            flexWrap: "wrap",
            gap: "37.3px",
            p: 0,
            mx: "auto",
            my: "20px",
          }}
        >
          {performances.map((performance) => (
            <MainPerformanceCard
              key={performance.id}
              performanceInfo={performance}
            />
          ))}
        </Box>
      ) : (
        <Box
          sx={{
            width: "100%",
            mt: "100px",
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
            gap: "10px",
            mb: "150px",
          }}
        >
          <SearchOff
            sx={{
              fontSize: { xs: "80px", md: "150px" },
            }}
          />
          <Typography level="body-lg">검색 결과가 없습니다.</Typography>
          <Button size="lg" color="neutral" onClick={() => navigate("/")}>
            메인으로
          </Button>
        </Box>
      )}
    </Box>
  );
};

export default ResultList;
