import { PrismAsyncLight as SyntaxHighlighter } from 'react-syntax-highlighter';
import { atomDark } from 'react-syntax-highlighter/dist/esm/styles/prism';
import jsx from 'react-syntax-highlighter/dist/esm/languages/prism/jsx';
import markup from 'react-syntax-highlighter/dist/esm/languages/prism/markup';
import { useToast, useClipboard, useEditable } from '@chakra-ui/react';
import copy from 'clipboard-copy';
import { useEffect } from 'react';

SyntaxHighlighter.registerLanguage('jsx', jsx);
SyntaxHighlighter.registerLanguage('xml', markup);

function CopyIcon(props: { className?: string; text: string }) {
  const { onCopy, setValue } = useClipboard(props.text);
  const toast = useToast();
  useEffect(() => {
    setValue(props.text);
  }, [props.text, setValue]);
  return (
    <svg
      onClick={() => {
        onCopy();
        toast({
          status: 'success',
          description: '复制成功',
          position: 'top',
          variant: 'subtle',
        });
      }}
      className={props.className}
      fill="#fff"
      width="32px"
      height="32px"
      viewBox="0 0 1920 1920"
    >
      <path
        d="M0 1919.887h1467.88V452.008H0v1467.88ZM1354.965 564.922v1242.051H112.914V564.922h1242.051ZM1920 0v1467.992h-338.741v-113.027h225.827V112.914H565.035V338.74H452.008V0H1920ZM338.741 1016.93h790.397V904.016H338.74v112.914Zm0 451.062h790.397v-113.027H338.74v113.027Zm0-225.588h564.57v-112.913H338.74v112.913Z"
        fillRule="evenodd"
      />
    </svg>
  );
}

export interface IPreviewCodeProps {
  code: string;
  copyable?: boolean;
  lang?: 'jsx' | 'xml';
}
export function PreviewCode(props: IPreviewCodeProps) {
  return (
    <div className="relative">
      <SyntaxHighlighter
        language={props.lang ?? 'jsx'}
        style={atomDark}
        showLineNumbers
        wrapLines
        wrapLongLines
      >
        {props.code}
      </SyntaxHighlighter>
      {props.copyable ? (
        <CopyIcon text={props.code} className="absolute top-0 right-0 p-2" />
      ) : null}
    </div>
  );
}
