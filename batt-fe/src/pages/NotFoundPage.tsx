import { Box, Button, Typography } from "@mui/joy";
import { useNavigate } from "react-router-dom";

const NotFoundPage = () => {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate("/", { replace: true });
  };

  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "1440px",
        mx: "auto",
        mt: "100px",
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        gap: "20px",
      }}
    >
      <Box
        sx={{
          width: "90%",
          maxWidth: "230px",
          p: "100px 32px 0",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          alignContent: "center",
        }}
      >
        <img src="/images/Logo.png" alt="BATT 캐릭터 로고" width="100%" />
      </Box>

      <Typography level="title-lg" textAlign="center">
        <span
          style={{
            fontSize: "50px",
            fontWeight: "bold",
            color: "#755345",
            justifySelf: "center",
          }}
        >
          Oops..!
        </span>
        <br />
        <br />
        잘못된 페이지에 접근하셨습니다.
      </Typography>
      <Button size="lg" color="neutral" onClick={handleClick}>
        메인으로
      </Button>
    </Box>
  );
};

export default NotFoundPage;
