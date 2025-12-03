import { Box } from "@mui/joy";
import React from "react";
import Select from "@mui/joy/Select";
import Option from "@mui/joy/Option";

type standard = {
  name: string;
  value: string;
};

const sortStandards: standard[] = [
  { name: "인기순", value: "rating_desc" },
  { name: "공연 임박순", value: "upcoming" },
  { name: "리뷰 많은순", value: "review_count" },
  { name: "최신순", value: "latest" },
];

interface Props {
  setSortType: (newSort: string) => void;
  currentSort: string;
}

const SortSelector: React.FC<Props> = ({ setSortType, currentSort }) => {
  const handleChange = (
    _: React.SyntheticEvent | null,
    newValue: string | null,
  ) => {
    if (newValue !== null) {
      setSortType(newValue);
    }
  };

  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "1152px",
        mx: "auto",
        display: "flex",
        justifyContent: "flex-end",
      }}
    >
      <Select
        defaultValue={sortStandards[0].value}
        value={currentSort}
        onChange={handleChange}
        sx={{
          width: "130px",
        }}
      >
        {sortStandards.map((standard) => (
          <Option key={standard.name} value={standard.value}>
            {standard.name}
          </Option>
        ))}
      </Select>
    </Box>
  );
};

export default SortSelector;
