import { useCallback, useEffect, useState } from 'react';
import {
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
  ButtonGroup,
  Button,
  Switch,
} from '@chakra-ui/react';

import { Input } from '@chakra-ui/react';

import { PrismAsyncLight as SyntaxHighlighter } from 'react-syntax-highlighter';
import jsx from 'react-syntax-highlighter/dist/esm/languages/prism/jsx';
import { FormRow } from '../components/FormRow';
import { ColorPicker } from '../components/ColorPicker';
import { PreviewCode } from '../components/PreviewCode';
import { DynamicSvgText, SvgText } from '../components/Font2Fvg';
import { IFontFace, getFontList } from '../api';

SyntaxHighlighter.registerLanguage('jsx', jsx);

const STATIC_IMPORT = `import { createComponent } from 'font2svg-react';
const SvgText = createComponent({ assertUrl: 'http://127.0.0.1:8888/asserts' });`;

const DYNAMIC_IMPORT = `import { createDynamicComponent } from 'font2svg-react';
const DynamicSvgText = createDynamicComponent({ assertUrl: 'http://127.0.0.1:8888/asserts' });`;
export interface IFontStyle {
  fontSize: number;
  color: string;
  fontFamily: string;
  underline: boolean;
}

export interface IPreviewFontsProps {
  onChange(fontFamily: string): void;
}
function PreviewFonts(props: IPreviewFontsProps) {
  const [fonts, setFonts] = useState<IFontFace[]>([]);
  useEffect(() => {
    getFontList().then((list) => {
      setFonts(list);
      props.onChange(list[0]?.name);
    });
  }, []);
  return (
    <div>
      {fonts.map((font) => (
        <SvgText
          key={font.id}
          onClick={() => props.onChange(font.name)}
          fontFamily={font.name}
          fontSize={20}
          text={font.previewText}
          color="blue"
          className="inline-block p-2 mb-4 mr-4 border radius-2"
        />
      ))}
    </div>
  );
}

export function PreviewPage() {
  const [fontStyle, setFontStyle] = useState({
    fontFamily: '',
    fontSize: 32,
    color: '#333',
    underline: false,
  });
  const [isStatic, setIsStatic] = useState(true);
  const [text, setText] = useState('预览文本');

  const componentCodeString = `<${isStatic ? 'SvgText' : 'DynamicSvgText'}
  fontFamily='${fontStyle.fontFamily}'
  fontSize={${fontStyle.fontSize}}
  color='${fontStyle.color}'
  text='${text}'
/>`;
  const updateFont = useCallback((key: string, value: string | number | boolean) => {
    console.log(key, value)
    setFontStyle((oldStyle) => ({ ...oldStyle, [key]: value }));
  }, []);

  return (
    <div>
      <FormRow label="预览字体">
        <PreviewFonts
          onChange={(fontFamily) => updateFont('fontFamily', fontFamily)}
        />
      </FormRow>
      <FormRow label="字体大小">
        <NumberInput
          value={fontStyle.fontSize}
          onChange={(fontSize) => updateFont('fontSize', fontSize)}
          display="inline"
        >
          <NumberInputField />
          <NumberInputStepper>
            <NumberIncrementStepper />
            <NumberDecrementStepper />
          </NumberInputStepper>
        </NumberInput>
      </FormRow>
      <FormRow label="字体颜色">
        <ColorPicker
          value={fontStyle.color}
          onChange={(color) => updateFont('color', color)}
        />
      </FormRow>
      <FormRow label="下划线">
        <Switch id='font-underline' value={fontStyle.underline} onChange={evt => updateFont('underline', evt.target.checked)} />
      </FormRow>

      <FormRow
        label="组件类型"
        placeholder="1. 静态组件值: 获取远程静态SVG，浏览器计算字体高度; 2. 动态组件: 将字体大小、颜色等参数传给服务器，由服务器动态生成SVG"
      >
        <ButtonGroup>
          <Button
            colorScheme="blue"
            variant={isStatic ? 'solid' : 'outline'}
            onClick={() => setIsStatic(true)}
          >
            静态组件
          </Button>
          <Button
            colorScheme="cyan"
            variant={!isStatic ? 'solid' : 'outline'}
            onClick={() => setIsStatic(false)}
          >
            动态组件
          </Button>
        </ButtonGroup>
      </FormRow>
      <FormRow label="预览文本">
        <Input
          className="w-auto"
          width="auto"
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
      </FormRow>
      <FormRow label="使用方式">
        <h3>引入方式</h3>

        <PreviewCode code={isStatic ? STATIC_IMPORT : DYNAMIC_IMPORT} />

        <h3>使用组件</h3>
        <div className="relative">
          <PreviewCode code={componentCodeString} copyable />
        </div>
      </FormRow>

      <FormRow label="效果预览">
        <div className="flex justify-center p-4 border border-black border-dashed">
          {isStatic ? (
            <SvgText
              fontFamily={fontStyle.fontFamily}
              fontSize={fontStyle.fontSize}
              color={fontStyle.color}
              text={text}
              underline={fontStyle.underline}
            />
          ) : (
            <DynamicSvgText
              fontFamily={fontStyle.fontFamily}
              fontSize={fontStyle.fontSize}
              color={fontStyle.color}
              text={text}
              underline={fontStyle.underline}
            />
          )}
        </div>
      </FormRow>
    </div>
  );
}
