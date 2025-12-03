import * as React from "react";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import PerformanceReview from "./PerformanceReview";
import SalesInfo from "./SalesInfo";
import type { PerformanceData } from "../../types/PerformanceData";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function CustomTabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

function a11yProps(index: number) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

interface PerformanceDetailProps {
  performanceData: PerformanceData;
}

export default function BasicTabs({ performanceData }: PerformanceDetailProps) {
  const [value, setValue] = React.useState(0);

  const handleChange = (_: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  return (
    <Box sx={{ width: "100%" }}>
      <Box
        sx={{
          borderBottom: 1,
          borderColor: "divider",
          position: "sticky",
          top: 0,
          backgroundColor: "white",
          zIndex: 10,
        }}
      >
        <Tabs
          value={value}
          onChange={handleChange}
          aria-label="basic tabs example"
          textColor="inherit"
          sx={{
            "& .MuiTabs-indicator": {
              backgroundColor: "black",
            },
            "& .MuiTab-root.Mui-selected": {
              color: "black",
              fontWeight: "bold",
            },
          }}
        >
          <Tab label="공연정보" {...a11yProps(0)} />
          <Tab label="위치정보" {...a11yProps(1)} />
          <Tab label="관람후기" {...a11yProps(2)} />
        </Tabs>
      </Box>
      <CustomTabPanel value={value} index={0}>
        <img
          src={performanceData.descriptionUrl}
          alt={`${performanceData.title} description`}
          style={{ width: "100%" }}
        />
      </CustomTabPanel>
      <CustomTabPanel value={value} index={1}>
        <SalesInfo />
      </CustomTabPanel>
      <CustomTabPanel value={value} index={2}>
        <PerformanceReview performanceId={performanceData.id} />
      </CustomTabPanel>
    </Box>
  );
}
