import * as React from "react";
import { IMaskInput } from "react-imask";

interface Props {
  onChange: (event: { target: { name: string; value: string } }) => void;
  name: string;
}

export const PhoneMaskAdapter = React.forwardRef<HTMLInputElement, Props>(
  function PhoneMaskAdapter(props, ref) {
    const { onChange, ...other } = props;
    return (
      <IMaskInput
        {...other}
        mask="000-0000-0000"
        definitions={{
          "0": /[0-9]/,
        }}
        inputRef={ref}
        onAccept={(value) => {
          const numericOnly = value.replace(/\D/g, ""); // 숫자만 남김
          onChange({ target: { name: props.name, value: numericOnly } });
        }}
        overwrite
      />
    );
  },
);
