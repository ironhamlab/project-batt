import type { Seat } from "../../../../types/Seat";
import { Grid } from "@mui/material";
import SelectedSeatsList from "./SelectedSeatsList";
import SeatMap from "./SeatMap";
import type { SeatCoordinate } from "../../../../types/SeatCoordinate";
import type { BookingInfo } from "../../../../types/BookingInfo";

interface Props {
  seats: SeatCoordinate[];
  selectedSeats: Seat[];
  setInfo: React.Dispatch<React.SetStateAction<BookingInfo>>;
  onClick: () => void;
  handlePrevBtn: () => void;
}

export default function ChoosingSeats({
  seats,
  selectedSeats,
  setInfo,
  onClick,
  handlePrevBtn,
}: Props) {
  return (
    <Grid container spacing={1}>
      <Grid
        size={8}
        sx={{
          paddingLeft: "30px",
        }}
      >
        <SeatMap
          seats={seats}
          selectedSeats={selectedSeats}
          setInfo={setInfo}
        />
      </Grid>
      <Grid
        size={4}
        sx={{
          borderLeft: "1px solid #ccc",
          padding: "20px",
        }}
      >
        <SelectedSeatsList
          selectedSeats={selectedSeats}
          onRemoveSeat={(id) => {
            setInfo((prev) => {
              const updatedSeats = prev.selectedSeats.filter(
                (seat) => seat.seatId !== id,
              );
              const updatedPrice = updatedSeats.reduce(
                (sum, seat) => sum + seat.price,
                0,
              );
              return {
                ...prev,
                selectedSeats: updatedSeats,
                price: updatedPrice,
              };
            });
          }}
          onClick={onClick}
          handlePrevBtn={handlePrevBtn}
        />
      </Grid>
    </Grid>
  );
}
