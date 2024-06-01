import { useState } from 'react'
import {
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
} from '@chakra-ui/react'

import {
  Input,
} from '@chakra-ui/react'

import { PrismAsyncLight as SyntaxHighlighter } from 'react-syntax-highlighter';
import jsx from 'react-syntax-highlighter/dist/esm/languages/prism/jsx';
import { FormRow } from '../components/FormRow';
import { ColorPicker } from '../components/ColorPicker';
import { PreviewCode } from '../components/PreviewCode';
import { Font2Svg } from '../components/Font2Fvg';

SyntaxHighlighter.registerLanguage('jsx', jsx);

export interface IFontStyle {
  fontSize: number;
  color: string;
  fontFamily: string;
}

export function PreviewPage() {

  const [fontStyle, setFontStyle] = useState({ fontSize: 16, color: '#333' })
  const [text, setText] = useState('预览文本')

  const componentCodeString = `<Font2Svg
  fontFamily='JinBuTi'
  fontSize={${fontStyle.fontSize}}
  color='${fontStyle.color}'
  text='${text}'
/>`

  return (
    <div>
      <FormRow label='预览字体'>
        <Input className="w-auto" width='300px' />
      </FormRow>
      <FormRow label='字体大小'>
        <NumberInput
          value={fontStyle.fontSize}
          onChange={(fontSize) => setFontStyle((oldStyle) => ({ ...oldStyle, fontSize: parseInt(fontSize) }))}
          display='inline'
        >
          <NumberInputField width='auto' />
          <NumberInputStepper>
            <NumberIncrementStepper />
            <NumberDecrementStepper />
          </NumberInputStepper>
        </NumberInput>
      </FormRow>
      <FormRow label='字体颜色'>
        <ColorPicker
          value={fontStyle.color}
          onChange={(color) => setFontStyle((oldStyle) => ({ ...oldStyle, color }))}
        />
      </FormRow>
      <FormRow label='预览文本'>
        <Input
          className="w-auto"
          width='auto'
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
      </FormRow>
      <FormRow label='使用方式'>
          <h3>引入方式</h3>

          <PreviewCode code={`import { createComponent } from 'font2svg-react';
const Font2Svg = createComponent({ assertUrl: 'http://127.0.0.1:8888/asserts' });`} />

          <h3>使用组件</h3>
          <div className='relative'>
            <PreviewCode code={componentCodeString} copyable />
          </div>
      </FormRow>

      <FormRow label='效果预览'>
        <div className='p-4 flex justify-center border-dashed border border-black'>
          <Font2Svg
            fontFamily={'JinBuTi'}
            fontSize={fontStyle.fontSize}
            color={fontStyle.color}
            text={text}
          />

        </div>
      </FormRow>
    </div>
  );
}