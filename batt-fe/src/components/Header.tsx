import {
  Box,
  Input,
  Tab,
  TabList,
  Tabs,
  IconButton,
  Breadcrumbs,
  Link,
  Button,
} from "@mui/joy";
import { useNavigate, useLocation } from "react-router-dom";
import React, { useState } from "react";
import { Cached, Search } from "@mui/icons-material";
import { DeleteToken, refreshRemainCoin } from "../lib/api/auth";
import { useAuthStore } from "../stores/authStore";

const Header = () => {
  const accessToken = useAuthStore((state) => state.accessToken);
  const navigate = useNavigate();
  const location = useLocation();

  const currentPath = location.pathname;
  const tabMap = ["/", "/transfer"];
  const currentTab = tabMap.indexOf(currentPath);

  const [searchInput, setSearchInput] = useState<string>("");
  const [hovered, setHovered] = useState(false);

  const battPoint = useAuthStore((state) => state.coin);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchInput.trim()) {
      const keyword = searchInput.trim();
      setSearchInput("");
      navigate(`/search?keyword=${encodeURIComponent(keyword)}`);
    }
  };

  const handleLogout = async () => {
    await DeleteToken();
    navigate("/", { replace: true });
  };

  const handleRefresh = () => {
    refreshRemainCoin();
  };

  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "1440px",
        mx: "auto",
        display: "flex",
        position: "relative",
        flexDirection: "column",
        alignItems: "center",
        justifySelf: "center",
        p: 0,
        marginTop: "10px",
      }}
    >
      <Box
        sx={{
          width: "90%",
          height: "60px",
          m: 0,
          p: 0,
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Box onClick={() => navigate("/")}>
          <img src="/images/Logo.png" alt="BATT 캐릭터" height="40px" />
          <img src="/images/Logo(BATT).png" alt="BATT 로고 " height="30px" />
        </Box>
        <Box
          sx={{
            width: "30%",
            minWidth: "100px",
            maxWidth: "432px",
            position: "absolute",
            left: "50%",
            transform: "translateX(-50%)",
          }}
        >
          <form onSubmit={handleSearchSubmit}>
            <Input
              placeholder="공연명을 입력해 주세요"
              value={searchInput}
              variant="soft"
              size="md"
              sx={{
                width: "100%",
                "--Input-focusedThickness": 0,
              }}
              endDecorator={
                <IconButton type="submit" color="neutral">
                  <Search />
                </IconButton>
              }
              onChange={(e) => setSearchInput(e.target.value)}
            />
          </form>
        </Box>
        <Box>
          {accessToken ? (
            <Breadcrumbs separator="" sx={{ "--Breadcrumbs-gap": "10px" }}>
              <Button
                size="sm"
                sx={{
                  bgcolor: "transparent",
                  color: "#755345",
                  "&:hover": {
                    bgcolor: "transparent",
                  },
                }}
                endDecorator={
                  <Cached
                    sx={{
                      color: "#755345",
                      fontSize: "15px",
                    }}
                  />
                }
                onMouseEnter={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}
                onClick={handleRefresh}
              >
                <span>
                  {hovered
                    ? "새로고침"
                    : `${battPoint.toLocaleString("ko-KR")} BATT`}
                </span>
              </Button>
              <Link color="neutral" level="title-sm" href="/mypage">
                마이 페이지
              </Link>
              <Link
                color="neutral"
                level="title-sm"
                onClick={handleLogout}
                style={{ cursor: "pointer" }}
              >
                로그아웃
              </Link>
            </Breadcrumbs>
          ) : (
            <Breadcrumbs>
              <Link
                color="neutral"
                level="title-sm"
                onClick={() =>
                  localStorage.setItem(
                    "redirectPath",
                    window.location.pathname + window.location.search,
                  )
                }
                href="/login"
              >
                로그인
              </Link>
            </Breadcrumbs>
          )}
        </Box>
      </Box>
      <Box
        sx={{
          width: "100%",
          height: "60px",
          m: 0,
          p: 0,
          display: "flex",
          justifyContent: "center",
        }}
      >
        <Tabs
          aria-label="tabs"
          size="lg"
          value={currentTab}
          sx={{
            "--Tabs-spacing": "25px",
            bgcolor: "transparent",
          }}
          onChange={(_, v) => navigate(tabMap[v as number])}
        >
          <TabList
            sx={{
              height: "60px",
              justifyContent: "center",
            }}
            underlinePlacement="top"
          >
            <Tab
              variant="plain"
              sx={{
                bgcolor: "transparent",
                "&.Mui-selected": {
                  bgcolor: "transparent",
                  fontWeight: "bold",
                },
              }}
              indicatorPlacement="top"
            >
              홈
            </Tab>
            <Tab
              variant="plain"
              sx={{
                bgcolor: "transparent",
                "&.Mui-selected": {
                  bgcolor: "transparent",
                  fontWeight: "bold",
                },
              }}
              indicatorPlacement="top"
            >
              양도
            </Tab>
          </TabList>
        </Tabs>
      </Box>
    </Box>
  );
};

export default Header;
