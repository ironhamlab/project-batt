import { Box } from "@mui/joy";
import { Checkbox, FormControlLabel } from "@mui/material";
import styles from "../../../common/Modal.module.css";
import React, { useEffect } from "react";

interface Props {
  setUserInfo: (field: "agreed", value: boolean) => void;
}
export default function TsAndCs({ setUserInfo }: Props) {
  const [checked, setChecked] = React.useState([false, false, false]);
  const handleAllChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setChecked([
      event.target.checked,
      event.target.checked,
      event.target.checked,
    ]);
  };
  const handleSingleChange =
    (idx: number) => (event: React.ChangeEvent<HTMLInputElement>) => {
      const newChecked = [...checked];
      newChecked[idx] = event.target.checked;
      setChecked(newChecked);
    };
  const allChecked = checked.every((value) => value === true);
  useEffect(() => {
    setUserInfo("agreed", allChecked);
  }, [checked]);

  const terms = (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
      }}
    >
      <FormControlLabel
        required
        label="[필수] 예매 및 취소 수수료/취소기한을 확인하였으며 동의합니다."
        control={
          <Checkbox checked={checked[0]} onChange={handleSingleChange(0)} />
        }
      />
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <div
          style={{
            minWidth: "300px",
          }}
          className={styles.warningSection}>

          <div className={styles.feeInfo}
            style={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "space-between",
              textAlign: "center",
            }}
          >
            <div>
              <p>
                예매 당일 ~ 공연 8일 전:
              </p>
              <p>
                공연 7-6일 전:
              </p>
              <p>
                공연 5-4일 전:
              </p>
              <p>
                공연 3-1일 전:
              </p>
              <p>
                공연 당일:
              </p>
            </div>
            <div>
              <p>
                <strong>0%</strong> 수수료
              </p>
              <p>
                <strong>10%</strong> 수수료
              </p>
              <p>
                <strong>20%</strong> 수수료
              </p>
              <p>
                <strong>30%</strong> 수수료
              </p>
              <p>
                <strong>취소 불가</strong>
              </p>
            </div>
          </div>
        </div>
      </div>
      <FormControlLabel
        required
        label="[필수] 개인정보 수집/이용에 동의합니다."
        control={
          <Checkbox checked={checked[1]} onChange={handleSingleChange(1)} />
        }
      />
      <FormControlLabel
        required
        label="[필수] 개인정보 제3자 제공 동의 및 주의사항"
        control={
          <Checkbox checked={checked[2]} onChange={handleSingleChange(2)} />
        }
      />
    </Box>
  );
  return (
    <div
      style={{
        padding: "20px 10px",
      }}
    >
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <h3>약관 동의</h3>
        <FormControlLabel
          label="전체동의"
          control={<Checkbox checked={allChecked} onChange={handleAllChange} />}
          sx={{}}
        ></FormControlLabel>
      </div>
      {terms}
    </div>
  );
}
