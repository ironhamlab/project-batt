import { Typography } from "@mui/joy";
import { Box } from "@mui/joy";

interface Props {
  title: string;
  venue: string;
  date: String;
  time: String;
  posterUrl?: string;
}
export default function Header({ title, venue, posterUrl, date, time }: Props) {
  
  return (
    <Box
      sx={{
        position: "relative",
        boxSizing: "border-box",
        width: "100%",
        height: "100px",
        margin: 0,
        overflow: "hidden",
      }}
    >
      <Box
        component="img"
        src={posterUrl || "../../assets/poster.png"}
        alt="뮤지컬 포스터"
        sx={{
          position: "absolute",
          width: "100%",
          height: "100%",
          top: 0,
          left: 0,
          right: 0,
          objectFit: "cover",
          filter: "blur(10px)",
          zIndex: 1,
        }}
      />
      <Box
        sx={{
          position: "absolute",
          top: 0,
          left: 0,
          right: 0,
          width: "100%",
          height: "100%",
          backgroundColor: "rgba(0, 0, 0, 0.5)",
          zIndex: 2,
        }}
      />
      <Box
        sx={{
          position: "absolute",
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          alignItems: "flex-end",
          bottom: 0,
          left: 0,
          padding: "20px",
          width: "100%",
          color: "white",
          textAlign: "center",
          zIndex: 3,
        }}
      >
        <Typography
          level="h1"
          sx={{ color: "white", fontSize: "24px", fontWeight: "bold" }}
        >
          {title}
        </Typography>
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            alignItems: "flex-end",
            gap: "10px",
          }}
        >
          <Typography level="body-md" sx={{ color: "white" }}>
            {date} {time} <br />
            {venue}
          </Typography>
          {/* <Button
            color="warning"
            disabled={false}
            onClick={function () {}}
            size="sm"
            variant="soft"
            style={{ marginBottom: "20px" }}
          >
            일정변경
          </Button> */}
        </div>
      </Box>
    </Box>
  );
}
