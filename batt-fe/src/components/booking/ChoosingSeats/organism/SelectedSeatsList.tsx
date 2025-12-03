import SelectedSeatCard from "../molecules/SelectedSeatCard";
import NextButton from "../atom/NextButton";
import type { Seat } from "../../../../types/Seat";
import { Button } from "@mui/joy";

interface Props {
  selectedSeats: Seat[];
  onRemoveSeat: (id: number) => void;
  onClick: () => void;
  handlePrevBtn : () => void;
}

export default function SelectedSeatsList({
  selectedSeats,
  onRemoveSeat,
  onClick,
  handlePrevBtn,
}: Props) {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        gap: "20px",
      }}
    >
      <div
        style={{
          height: "500px",
          flexGrow: 1,
          display: "flex",
          flexDirection: "column",
          gap: "20px",
        }}
      >
        <h2>
          선택한 좌석 <span>{selectedSeats.length}</span>
        </h2>
        <div
          style={{
            overflowY: "auto",
          }}
        >
          {selectedSeats.map((seat) => (
            <SelectedSeatCard
              key={seat.seatId}
              seat={seat}
              onRemove={onRemoveSeat}
            />
          ))}
          {selectedSeats.length === 0 && (
            <p>선택된 좌석이 없습니다. 좌석을 선택해주세요.</p>
          )}
        </div>
      </div>
      <div
        style={{
          bottom: "0",
          width: "100%",
          left: "0",
          right: "0",
          padding: "0 20px",
          display: "flex",
          flexDirection: "row",
          justifyContent: "center",
          marginTop: "20px",
          gap: "10px"
        }}
      >
        <Button
          fullWidth
          // sx={{
          //     width: "100%",
          //     fontWeight: "bold",
          // }}
          variant="outlined"
          onClick={handlePrevBtn}
        >
          이전
        </Button>
        <NextButton
          isAble={selectedSeats.length > 0}
          onClick={onClick}
          text="다음"
        />
      </div>
    </div>
  );
}
