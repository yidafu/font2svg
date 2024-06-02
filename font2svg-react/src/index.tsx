import React, { useEffect, useRef, useState } from 'react'
import { parse, IElement, stringify } from './svg';

export interface ICreateComponentOptions {
  assertUrl: string;
}

export interface IFont2SvgProps {
  className?: string;
  onClick?: React.MouseEventHandler<HTMLSpanElement>,
  fontFamily: string;
  text: string;
  color?: string;
  fontSize?: number;
  fallback?: boolean;
}

function calcSvg(svg: string, fontSize: number): string {
  if (svg.length === 0) return ''

  const rootObj: IElement = parse(svg)
  const svgObj = rootObj.children[0]
  svgObj.properties['fill'] = 'currentColor'
  svgObj.properties['style'] = 'display: inline;'
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

function toPascalCase(str: string) {
  return str.replace(/-(\w)/g, (_, c) => c.toUpperCase())
}

function buildJsxElement(node: IElement, key: string) {
  if (node.tagName === 'text') return node.value
  const props = Object.entries(node.properties ?? {})
    .map<[string, string]>(([k, v]) => [toPascalCase(k), v as string])
    .reduce((acc, [k, v]) => {acc[k] = v; return acc}, {} as Record<string, string>)
  props.key = key
  return React.createElement(
    node.tagName,
    props,
    node.children?.map((child, index) => buildJsxElement(child, index)) ?? [])
}

function Svg2Jsx(props: { svg: string, size: number }) {
  const { svg, size } = props;

  if (svg.length === 0) return ''

  const rootObj: IElement = parse(svg)
  const svgObj = rootObj.children[0]
  svgObj.properties['fill'] = 'currentColor'
  svgObj.properties['style'] = { display: 'inline' }
  const viewBox = svgObj.properties.viewBox ?? ''
  const heightStr = viewBox.trim().split(' ').at(-1) ?? '1000'
  const widthStr = viewBox.trim().split(' ').at(-2) ?? '1000'
  const height = parseInt(heightStr, 10)
  const fontHeight = height * size / 1000
  const fontWidth = parseInt(widthStr, 10) / height * fontHeight
  svgObj.properties.height = fontHeight + 'px'
  svgObj.properties.width = fontWidth + 'px'
  return buildJsxElement(svgObj, '0');
}

export function createComponent(option: ICreateComponentOptions) {

  function buildSvgUrl(fontFamily: string, char: string) {
    const charCode = char.charCodeAt(0)
    return `${option.assertUrl}/svg/${fontFamily}/${charCode}.svg`;
  }
  return function Font2Svg(props: IFont2SvgProps) {
    const { fontFamily, text, color = '#000', fontSize = 16, fallback } = props;
    const [originSvgList, setOriginSvgList] = useState<string[]>([])
    useEffect(() => {
      async function fetchSvg() {
        if (!fontFamily) return;
        if (!text) return;
        try {

          const promises = text.split('').map<Promise<string>>(char => {
            const svgUrl = buildSvgUrl(fontFamily, char)
            return fetch(svgUrl).then(res => res.text())
          })
          const svgTextList = await Promise.all(promises)

          setOriginSvgList(svgTextList.filter(svg => svg.length > 0))
        } catch (e) {
          console.error(e)
        }

      }

      fetchSvg()
    }, [text, fontFamily])

    return <span
      onClick={props.onClick}
      style={{ color }}
      className={props.className ?? ''}
    >
      {originSvgList.map((svg,index) => <Svg2Jsx key={index} svg={svg} size={fontSize}/>)}
    </span>
  }
}

export function createDynamicComponent(option: ICreateComponentOptions) {
  function buildDynamicSvgUrl(fontFamily: string, char: string, fontSize: number, color: string) {
    const charCode = char.charCodeAt(0)
    return `${option.assertUrl}/dynamic/svg/${fontFamily}/${charCode}.svg?fontSize=${fontSize}&color=${color}`;
  }
  return function Font2Svg(props: IFont2SvgProps) {
    const { fontFamily, text, color = '#000', fontSize = 16, fallback } = props;

    return <span
      onClick={props.onClick}
      className={props.className ?? ''}>
      {text.split('').map(char => {
        const svgUrl = buildDynamicSvgUrl(fontFamily, char, fontSize, color)
        return <img key={char} style={{ display: 'inline-block' }} src={svgUrl} alt={char} />
      })}
    </span>
  }
}
