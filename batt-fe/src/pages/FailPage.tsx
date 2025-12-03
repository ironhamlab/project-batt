import { useNavigate, useSearchParams } from "react-router-dom";
import style from "../styles/Payment.module.css";
import { Button } from "@mui/joy";

export function FailPage() {
  const [searchParams] = useSearchParams();
  const performanceId = sessionStorage.getItem("performanceId");
  const navigate = useNavigate();
  return (
    <div id="info" className={style.box_section} style={{ width: "600px" }}>
      <img
        width="100px"
        src="https://static.toss.im/lotties/error-spot-no-loop-space-apng.png"
        alt="에러 이미지"
      />
      <h2>결제를 실패했어요</h2>

      <div
        className={`${style["p-grid"]} ${style["typography--p"]}`}
        style={{ marginTop: "50px" }}
      >
        <div className={`${style["p-grid-col"]} ${style["text--left"]}`}>
          <b>에러메시지</b>
        </div>
        <div
          className={`${style["p-grid-col"]} ${style["text--right"]}`}
          id="message"
        >
          {`${searchParams.get("message")}`}
        </div>
      </div>
      <div
        className={`${style["p-grid"]} ${style["typography--p"]}`}
        style={{ marginTop: "10px" }}
      >
        <div className={`${style["p-grid-col"]} ${style["text--left"]}`}>
          <b>에러코드</b>
        </div>
        <div
          className={`${style["p-grid-col"]} ${style["text--right"]}`}
          id="code"
        >
          {`${searchParams.get("code")}`}
        </div>
      </div>

      <div className={style["p-grid-col"]}>
        <Button onClick={() => navigate(`/performance/${Number(performanceId)}`)}>
          처음으로
        </Button>
        <Button onClick={() => navigate("/")}>
          홈
        </Button>

      </div>
    </div>
  );
}
