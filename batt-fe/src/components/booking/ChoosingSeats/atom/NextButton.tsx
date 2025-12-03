import { Button } from "@mui/joy";

interface Props {
  isAble: boolean;
  text: string;
  onClick: () => void;
}

export default function NextButton({ isAble, text, onClick }: Props) {
  return (
    <Button
      fullWidth
      // sx={{
      //     width: "100%",
      //     fontWeight: "bold",
      // }}
      disabled={!isAble}
      variant="solid"
      onClick={onClick}
    >
      {text}
    </Button>
  );
}
