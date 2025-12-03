import { useEffect, useState } from "react";
import Box from "@mui/joy/Box";
import MainPerformanceList from "../components/mainpage/MainPerformanceList";
import SortSelector from "../components/SortSelector";
import apiClient from "../lib/axios";
import type { Performance } from "../types/Performance";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { Pagination } from "@mui/material";
import { useSearchParams } from "react-router-dom";

// interface AuthState {
//   state: {
//     accessToken: string;
//     memberId: number;
//     coin: number;
//   };
//   version: number;
// }


const settings = {
  dots: true,
  infinite: true,
  speed: 500,
  slidesToShow: 1,
  slidesToScroll: 1,
  autoplay: true,
  autoplaySpeed: 10000,
  pauseOnHover: false,
  pauseOnFocus: false,
  arrows: false,
};

type imageInfo = {
  id: number;
  title: string;
  src: string;
  alt: string;
};

const images: imageInfo[] = [
  {
    id: 1,
    title: "transferAdv",
    src: "/mainCarousel/transferAdv.png",
    alt: "메인 배너 양도 홍보",
  },
  {
    id: 2,
    title: "mobileTicketAdv",
    src: "/mainCarousel/mobileAdv.png",
    alt: "모바일 티켓 홍보",
  },
  {
    id: 3,
    title: "pointAdv",
    src: "/mainCarousel/pointAdv.png",
    alt: "바티 포인트 홍보",
  },
];

const pageLimit = 12;

const MainPage = () => {

  // const authString = sessionStorage.getItem("auth");

  // if (authString) {
  //   const authObj: AuthState = JSON.parse(authString);

    // console.log(authObj.state.accessToken); // string
    // console.log(authObj.state.memberId);    // number
    // console.log(authObj.state.coin);        // number
  // }

  const [searchParams, setSearchParams] = useSearchParams();
  const page = parseInt(searchParams.get("page") || "1", 10);
  const sortType = searchParams.get("sort") || "rating_desc";

  const [totalPages, setTotalPages] = useState<number>(0);
  const [performances, setPerformances] = useState<Performance[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const currentPage = parseInt(searchParams.get("page") || "1");

    const fetchData = async () => {
      setIsLoading(true);
      try {
        const response = await apiClient.get(
          `/api/v1/performances?sort=${sortType}&page=${currentPage}&limit=${pageLimit}&status=OPEN&status=SCHEDULE`,
        );
        setTotalPages(response.data.pageInfo.totalPages);
        setPerformances(response.data.performances);
      } catch (error) {
        console.log("공연 정보를 불러오는 데 실패했습니다.", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [sortType, searchParams]);

  const handleSortChange = (newSort: string) => {
    setSearchParams({ sort: newSort, page: "1" });
  };

  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setSearchParams({ sort: sortType, page: value.toString() });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "1440px",
        mx: "auto",
        p: 0,
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        overflowX: "hidden",
      }}
    >
      <Box
        className="slider-container"
        sx={{
          width: "100%",
          maxWidth: "1440px",
          mx: "auto",
          p: 0,
          mb: "20px",
          position: "relative",
        }}
      >
        <Slider {...settings}>
          {images.map((image) => (
            <Box key={image.id}>
              <img width="100%" src={image.src} alt={image.alt} />
            </Box>
          ))}
        </Slider>
      </Box>
      <SortSelector setSortType={handleSortChange} currentSort={sortType} />
      {isLoading ? (
        <p>공연 목록을 불러오는 중입니다</p>
      ) : (
        <MainPerformanceList performances={performances} />
      )}
      {performances.length > 0 && (
        <Pagination
          count={totalPages}
          page={page}
          onChange={handlePageChange}
        />
      )}
    </Box>
  );
};

export default MainPage;
