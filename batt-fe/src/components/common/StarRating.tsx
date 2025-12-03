import Rating from "@mui/material/Rating";
import type { SxProps, Theme } from "@mui/material/styles";

interface StarRatingProps {
  rating: number;
  setRating?: (rating: number) => void;
  readOnly?: boolean;
  size?: "small" | "medium" | "large";
}

const StarRating: React.FC<StarRatingProps> = ({
  rating,
  setRating,
  readOnly = false,
  size = "medium",
}) => {
  const valueIn5Scale = readOnly ? Math.round(rating) / 2 : rating / 2;

  const handleChange = (
    _event: React.SyntheticEvent,
    newValue: number | null,
  ) => {
    if (setRating && newValue !== null) {
      setRating(newValue * 2);
    }
  };

  const sxProps: SxProps<Theme> = {
    "& .MuiRating-iconFilled": {
      color: "#FFD700",
    },
    "& .MuiRating-iconHover": {
      color: "#FFC700",
    },
  };

  return (
    <Rating
      name="customized-star-rating"
      value={valueIn5Scale}
      onChange={handleChange}
      precision={0.5}
      max={5}
      readOnly={readOnly}
      size={size}
      sx={sxProps}
    />
  );
};

export default StarRating;
