import { Card } from "@mui/joy";
import { IconButton } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import type { Seat } from "../../../../types/Seat";

interface Props {
  seat: Seat;
  onRemove: (id: number) => void;
}

export default function SelectedSeatCard({ seat, onRemove }: Props) {
  return (
    <Card
      variant="plain"
      color="neutral"
      sx={{
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "10px 20px",
        marginBottom: "10px",
        width: "100%",
      }}
    >
      <div>
        <h4>{seat.grade}석</h4>
        <p>{seat.seatNumber}</p>
      </div>
      <h4>
        {seat.price}원
        <IconButton aria-label="delete">
          <DeleteIcon onClick={() => onRemove(seat.seatId)} />
        </IconButton>
      </h4>
    </Card>
  );
}
