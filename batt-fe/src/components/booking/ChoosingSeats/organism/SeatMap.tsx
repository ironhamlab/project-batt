import { IconButton } from "@mui/joy";
import type { Seat } from "../../../../types/Seat";
import SeatItem from "../molecules/SeatItem";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import { useState } from "react";
import type { SeatCoordinate } from "../../../../types/SeatCoordinate";
import type { BookingInfo } from "../../../../types/BookingInfo";

interface Props {
  seats: SeatCoordinate[];
  selectedSeats: Seat[];
  setInfo: React.Dispatch<React.SetStateAction<BookingInfo>>;
}

export default function SeatMap({ seats, selectedSeats, setInfo }: Props) {
  const [scale, setScale] = useState(1);
  const toggle = (seat: Seat) => {
    if (selectedSeats.length >= 4) {
      alert("좌석은 최대 4개까지 선택할 수 있습니다.");
      return;
    }

    setInfo((prev) => {
      const isSelected = prev.selectedSeats.find(
        (s) => s.seatId === seat.seatId,
      );
      const updatedSeats = isSelected
        ? prev.selectedSeats.filter((s) => s.seatId !== seat.seatId)
        : [...prev.selectedSeats, seat];
      const updatedPrice = updatedSeats.reduce((sum, s) => sum + s.price, 0);
      return {
        ...prev,
        selectedSeats: updatedSeats,
        price: updatedPrice,
      };
    });
  };
  return (
    <div>
      <div
        style={{
          width: "100%",
          height: "auto",
          overflow: "scroll",
        }}
      >
        <div style={{ transform: `scale(${scale})`, transformOrigin: "0 0" }}>
          <svg
            viewBox="0 0 1000 600"
            width={800}
            height={500}
            preserveAspectRatio="xMidYMid meet"
          >
            {seats.map((seat) => (
              <SeatItem
                key={seat.id}
                id={seat.id}
                isAvailable={seat.status}
                x={Number(seat.x) + 5}
                y={Number(seat.y) + 5}
                seatGrade={seat.seat.grade}
                isSelected={selectedSeats.some(
                  (s) => s.seatId === seat.seat.seatId,
                )}
                onClick={() => {
                  toggle(seat.seat);
                  console.log(`Seat ${seat.id} clicked`);
                }}
              />
            ))}
          </svg>
        </div>
      </div>
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "center",
        }}
      >
        <IconButton aria-label="zoom-in">
          <AddIcon
            onClick={() => setScale((prev) => Math.min(prev + 0.2, 3))}
          />
        </IconButton>
        <IconButton aria-label="zoom-out">
          <RemoveIcon
            onClick={() => setScale((prev) => Math.max(prev - 0.2, 0.5))}
          />
        </IconButton>
      </div>
    </div>
  );
}
