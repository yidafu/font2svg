import React, { useEffect, useRef, useState } from 'react'
import { parse, IElement, stringify } from './svg';

export interface ICreateComponentOptions {
  assertUrl: string;
}

export interface IFont2SvgProps {
  fontFamily: string;
  text: string;
  color?: string;
  fontSize?: number;
  fallback?: boolean;
}

function calcSvg(svg: string, fontSize: number): string {

  const rootObj: IElement = parse(svg)
  const svgObj = rootObj.children[0]
  const viewBox = svgObj.properties.viewBox ?? ''
  const heightStr = viewBox.trim().split(' ').at(-1) ?? '1000'
  const widthStr = viewBox.trim().split(' ').at(-2) ?? '1000'
  const height = parseInt(heightStr, 10) 
  const fontHeight = height * fontSize / 1000
  const fontWidth = parseInt(widthStr, 10) / height * fontHeight
  svgObj.properties.height = fontHeight + 'px'
  svgObj.properties.width = fontWidth + 'px'
  return stringify(rootObj)
}

export function createComponent(option: ICreateComponentOptions) {

  function buildSvgUrl(fontFamily: string, char: string) {
    const charCode = char.charCodeAt(0)
    return `${option.assertUrl}/svg/${fontFamily}/${charCode}.svg`;
  }
  return function Font2Svg(props: IFont2SvgProps) {
    const { fontFamily, text, color = '#000', fontSize = 16, fallback } = props;
    const [svgHtml, setSvgHtml] = useState('')
    useEffect(() => {
      async function fetchSvg() {
        const promises = text.split('').map<Promise<string>>(char => {
          const svgUrl = buildSvgUrl(fontFamily, char)
          return fetch(svgUrl).then(res => res.text())
        })
        const svgTextList = await Promise.all(promises)

        const html = svgTextList.map(svg => calcSvg(svg, fontSize)).join('')
        setSvgHtml(html)
      }

      fetchSvg()
    }, [text])
    return <div style={{ color }} dangerouslySetInnerHTML={{ __html: svgHtml }}></div>
  }
}

export function createDynamicComponent(option: ICreateComponentOptions) {
  function buildDynamicSvgUrl(fontFamily: string, char: string, fontSize: number, color: string) {
    const charCode = char.charCodeAt(0)
    return `${option.assertUrl}/dynamic/svg/${fontFamily}/${charCode}.svg?fontSize=${fontSize}&color=${color}`;
  }
  return function Font2Svg(props: IFont2SvgProps) {
    const { fontFamily, text, color = '#000', fontSize = 16, fallback } = props;

    return <div>
      {text.split('').map(char => {
        const svgUrl = buildDynamicSvgUrl(fontFamily, char, fontSize, color)
        return <img src={svgUrl} alt={char} />
      })}
    </div>
  }
}
