import { Box, Button, Typography, Link } from "@mui/joy";

const baseURL = import.meta.env.VITE_API_URL || "http://localhost:8080";

const LoginPage = () => {
  const handleKakaoLogin = async () => {
    console.log("카카오로그인 요청");
    try {
      window.location.replace(`${baseURL}/oauth2/authorization/kakao`);
    } catch (error) {
      console.log("로그인 요청 실패:", error);
    }
  };

  const handleNaverLogin = async () => {
    console.log("네이버로그인 요청");
    try {
      window.location.replace(`${baseURL}/oauth2/authorization/naver`);
    } catch (error) {
      console.log("로그인 요청 실패:", error);
    }
  };

  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "600px",
        mx: "auto",
        p: 0,
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
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
        <img src="/images/Logo(BATT).png" alt="BATT 로고" width="100%" />
      </Box>
      <Box
        sx={{
          width: "90%",
          maxWidth: "536px",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          gap: "10px",
          mt: "80px",
        }}
      >
        <Button
          variant="outlined"
          color="neutral"
          sx={{
            width: "100%",
            p: "12px 16px",
          }}
          onClick={handleKakaoLogin}
        >
          <Typography
            sx={{
              position: "relative",
              width: "100%",
              fontSize: "16px",
            }}
          >
            <img
              src="/images/logo_kakao_20.svg"
              style={{ position: "absolute", left: 0 }}
            />
            카카오로 시작하기
          </Typography>
        </Button>
        <Button
          variant="outlined"
          color="neutral"
          sx={{
            width: "100%",
            p: "12px 16px",
          }}
          onClick={handleNaverLogin}
        >
          <Typography
            sx={{
              position: "relative",
              width: "100%",
              fontSize: "16px",
            }}
          >
            <img
              src="/images/logo_naver_20.svg"
              style={{ position: "absolute", left: 0 }}
            />
            네이버로 시작하기
          </Typography>
        </Button>
      </Box>
      <Link
        color="neutral"
        underline="hover"
        href="/"
        sx={{
          mt: "20px",
        }}
      >
        메인으로
      </Link>
    </Box>
  );
};

export default LoginPage;
