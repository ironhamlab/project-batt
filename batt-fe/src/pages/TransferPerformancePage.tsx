import { useEffect, useState } from "react";
import TransferPerformanceList from "../components/transferPerformanceList/TransferPerformanceList";
import type { TransferPerformance } from "../types/Performance";
import { Box } from "@mui/joy";
import SortSelector from "../components/SortSelector";
import apiClient from "../lib/axios";
import { useSearchParams } from "react-router-dom";
import { Pagination } from "@mui/material";

const pageLimit = 10;

const sortTypeConvert = {
  rating_desc: "POPULAR",
  latest: "LATEST",
  upcoming: "PERFORMANCE_DATE",
  review_count: "REVIEW_COUNT",
} as const;

const TransferPerformancePage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const page = parseInt(searchParams.get("page") || "1", 10);
  const sortType = searchParams.get("sort") || "rating_desc";

  const [totalPages, setTotalPages] = useState<number>(0);
  const [performances, setPerformances] = useState<TransferPerformance[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const currentPage = parseInt(searchParams.get("page") || "1");
    const fetchData = async () => {
      setIsLoading(true);
      try {
        const sort = sortTypeConvert[sortType as keyof typeof sortTypeConvert];
        const response = await apiClient.get(
          `/api/v1/transfers?sort=${sort}&page=${currentPage - 1}&size=${pageLimit}`,
        );
        setTotalPages(response.data.pageInfo.totalPages);
        setPerformances(response.data.transfers);
      } catch (error) {
        console.error("공연 정보를 불러오는 데 실패했습니다.", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [sortType, page]);

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
      }}
    >
      <SortSelector setSortType={handleSortChange} currentSort={sortType} />

      {isLoading ? (
        <p>공연 목록을 불러오는 중입니다</p>
      ) : (
        <TransferPerformanceList performances={performances} />
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

export default TransferPerformancePage;
