import { FormControl, FormLabel, Input } from "@mui/joy";

interface Props {
  label: string;
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  name: string;
}
export default function InputField({ label, value, onChange, name }: Props) {
  return (
    <FormControl>
      <FormLabel>{label}</FormLabel>
      <Input value={value} onChange={onChange} name={name} fullWidth />
    </FormControl>
  );
}
