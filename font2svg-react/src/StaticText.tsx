import React, { useEffect, useRef, useState } from 'react';
import { parse, IElement, ISvgElement, IText, isText, isElement } from './svg.js';
import { ICreateComponentOptions, IFont2SvgProps } from './types.js';


function toPascalCase(str: string) {
  if (str.startsWith('data-')) return str;
  return str.replace(/-(\w)/g, (_, c) => c.toUpperCase());
}

function buildJsxElement(node: ISvgElement, key: string): React.ReactElement | string {
  if (isText(node)) return node.value;

  const props = Object.entries(node.properties ?? {})
    .map<[string, string]>(([k, v]) => [toPascalCase(k), v as string])
    .reduce(
      (acc, [k, v]) => {
        acc[k] = v;
        return acc;
      },
      {} as Record<string, string>
    );
  props.key = key;
  return React.createElement(
    node.tagName,
    props,
    node.children?.map((child, index) => buildJsxElement(child, index.toString())) ?? []
  );
}

function Svg2Jsx(props: { svg: string; size: number, underline: boolean }) {
  const { svg, size } = props;

  if (svg.length === 0) return '';

  const rootObj: IElement = parse(svg);
  const svgObj = rootObj.children[0];
  svgObj.properties['fill'] = 'currentColor';
  svgObj.properties['style'] = { display: 'inline' } as any;
  const viewBox = svgObj.properties.viewBox ?? '';
  const unit = parseInt(svgObj.properties['data-units-per-em'] as string ?? '1000', 10)
  const heightStr: string = viewBox.trim().split(' ').at(-1)!!;
  const widthStr = viewBox.trim().split(' ').at(-2)!!;
  const height = parseInt(heightStr, 10);
  const fontHeight = (height * size) / unit;
  const fontWidth = (parseInt(widthStr, 10) / height) * fontHeight;
  if (props.underline) {
    const offset = (-parseInt(svgObj.properties['data-underline-pos'], 10)).toString();
    const strokeWidth = svgObj.properties['data-underline-thickness']
    if (isElement(svgObj)) {
      svgObj.children.push({
        tagName: 'line',
        properties: {
          x1: '0',
          y1: offset,
          x2: widthStr,
          y2: offset,
          stroke: 'currentColor',
          strokeWidth,
        },
        children: [],
      })
    }
  }

  svgObj.properties.height = fontHeight + 'px';
  svgObj.properties.width = fontWidth + 'px';

  return buildJsxElement(svgObj, '0');
}

export function createComponent(option: ICreateComponentOptions) {
  function buildSvgUrl(fontFamily: string, char: string) {
    const charCode = char.charCodeAt(0);
    return `${option.assertUrl}/svg/${fontFamily}/${charCode}.svg`;
  }
  return function Font2Svg(props: IFont2SvgProps) {
    const { fontFamily, text, color = '#000', fontSize = 16, fallback } = props;
    const [originSvgList, setOriginSvgList] = useState<string[]>([]);
    useEffect(() => {
      async function fetchSvg() {
        if (!fontFamily) return;
        if (!text) return;
        try {
          const promises = text.split('').map<Promise<string>>((char) => {
            const svgUrl = buildSvgUrl(fontFamily, char);
            return fetch(svgUrl).then((res) => res.text());
          });
          const svgTextList = await Promise.all(promises);

          setOriginSvgList(svgTextList.filter((svg) => svg.length > 0));
        } catch (e) {
          console.error(e);
        }
      }

      fetchSvg();
    }, [text, fontFamily]);

    return (
      <span
        onClick={props.onClick}
        style={{ color }}
        className={props.className ?? ''}
      >
        {originSvgList.map((svg, index) => (
          <Svg2Jsx key={index} svg={svg} size={fontSize} underline={!!props.underline} />
        ))}
      </span>
    );
  };
}
