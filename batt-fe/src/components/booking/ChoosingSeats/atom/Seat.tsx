interface Props {
  x: number;
  y: number;
  onClick: () => void | undefined;
  isSelected: boolean;
  fillColor: string;
}

export default function Seat({ x, y, onClick, isSelected, fillColor }: Props) {
  return (
    <rect
      x={x}
      y={y}
      width={10}
      height={10}
      fill={fillColor}
      stroke={isSelected ? "black" : "none"}
      strokeWidth={2}
      rx={0}
      ry={0}
      onClick={() => onClick()}
      style={{ cursor: "pointer" }}
    />
  );
}
