import { Box, Typography } from "@mui/joy";
import ResultList from "../components/search/ResultList";
import type { Performance } from "../types/Performance";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import apiClient from "../lib/axios";
import SortSelector from "../components/SortSelector";
import { Pagination } from "@mui/material";

const pageLimit = 12;

const SearchResultPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const keyword = searchParams.get("keyword") || "";
  const page = parseInt(searchParams.get("page") || "1", 10);
  const sortType = searchParams.get("sort") || "rating_desc";

  const displayKeyword =
    keyword.length > 30 ? `${keyword.slice(0, 30)}...` : keyword;

  const [totalPages, setTotalPages] = useState<number>(0);
  const [performances, setPerformances] = useState<Performance[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const currentPage = parseInt(searchParams.get("page") || "1");

    const fetchData = async () => {
      setIsLoading(true);
      try {
        const response = await apiClient.get(
          `/api/v1/performances?keyword=${keyword}&sort=${sortType}&page=${currentPage}&limit=${pageLimit}`,
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
  }, [keyword, sortType, page]);

  const handleSortChange = (newSort: string) => {
    setSearchParams({ keyword, sort: newSort, page: "1" });
  };

  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setSearchParams({ keyword, sort: sortType, page: value.toString() });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "1440px",
        mx: "auto",
        mt: "50px",
        p: 0,
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Box
        sx={{
          width: "80%",
          mx: "auto",
          p: 0,
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
        }}
      >
        <Typography
          level="h3"
          sx={{
            borderBottom: "1px solid #c1c1c1ff",
            pb: "20px",
            mb: "20px",
          }}
        >
          "<span style={{ color: "#4A4A8C" }}>{displayKeyword}</span>" 검색결과
        </Typography>
        <SortSelector setSortType={handleSortChange} currentSort={sortType} />
        {isLoading ? (
          <p>공연 목록을 불러오는 중입니다</p>
        ) : (
          <ResultList performances={performances} />
        )}
      </Box>
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

export default SearchResultPage;
