import Seat from "../atom/Seat";

interface Props {
  id: number;
  x: number;
  y: number;
  seatGrade: string;
  isAvailable: boolean;
  isSelected: boolean;
  onClick: () => void;
}
const getColor = (seatGrade: string) => {
  switch (seatGrade) {
    case "VIP":
      return "#BA55D3"; // Purple
    case "R":
      return "#1E90FF"; // Blue
    case "S":
      return "#228B22"; // Green
    case "A":
      return "#FF7F50";
    case "B":
      return "#FFDEAD";
    default:
      return "#D3D3D3";
  }
};
export default function SeatItem({
  x,
  y,
  seatGrade,
  isSelected,
  isAvailable,
  onClick,
}: Props) {
  const fillColor = isAvailable ? getColor(seatGrade) : "#D3D3D3";
  return (
    <Seat
      x={x}
      y={y}
      fillColor={fillColor}
      isSelected={isSelected}
      onClick={() => {
        if (isAvailable) onClick();
      }}
    />
  );
}
