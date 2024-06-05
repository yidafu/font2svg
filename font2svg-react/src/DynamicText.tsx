import React from 'react';
import { ICreateComponentOptions, IFont2SvgProps } from './types.js';


export function createDynamicComponent(option: ICreateComponentOptions) {
  function buildDynamicSvgUrl(
    fontFamily: string,
    char: string,
    fontSize: number,
    color: string,
    underline: boolean,
  ) {
    const charCode = char.charCodeAt(0);
    return `${option.assertUrl}/dynamic/svg/${fontFamily}/${charCode}.svg?fontSize=${fontSize}&color=${color.replace('#', '%23')}&underline=${underline}`;
  }
  return function Font2Svg(props: IFont2SvgProps) {
    const { fontFamily, text, color = '#000', fontSize = 16, underline, fallback } = props;

    return (
      <span onClick={props.onClick} className={props.className ?? ''}>
        {text.split('').map((char) => {
          const svgUrl = buildDynamicSvgUrl(fontFamily, char, fontSize, color, !!underline);
          return (
            <img
              key={char}
              style={{ display: 'inline-block' }}
              src={svgUrl}
              alt={char}
            />
          );
        })}
      </span>
    );
  };
}
