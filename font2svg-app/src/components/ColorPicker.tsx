import { useState } from 'react';

import { ChromePicker, ColorResult } from 'react-color';

export interface IResponse<T> {
  message: string;
  code: string;
  data: T;
}

export interface IColorPickerProps {
  value: string;
  onChange: (color: string) => void;
}
export function ColorPicker(props: IColorPickerProps) {
  // const [color, setColor] = useState<ColorResult | null>(null)
  const [displayColorPicker, setDisplayColorPicker] = useState(false);

  const hexColor = props.value ?? '#333';

  return (
    <div className="inline">
      <div
        onClick={() => setDisplayColorPicker((b) => !b)}
        className="w-12 h-6 border-2 inline-block"
        style={{ borderColor: hexColor }}
      >
        <div
          className="w-10 h-4 m-0.5 inline-block"
          style={{ backgroundColor: hexColor }}
        ></div>
      </div>
      {displayColorPicker ? (
        <div className="absolute z-10 bg-white">
          <ChromePicker
            color={hexColor}
            onChange={(c: ColorResult) => props.onChange(c.hex)}
          />
        </div>
      ) : null}
    </div>
  );
}
