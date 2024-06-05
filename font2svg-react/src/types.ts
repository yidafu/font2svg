export interface ICreateComponentOptions {
  assertUrl: string;
}

export interface IFont2SvgProps {
  className?: string;
  onClick?: React.MouseEventHandler<HTMLSpanElement>;
  fontFamily: string;
  text: string;
  color?: string;
  fontSize?: number;
  underline?: boolean;
  fallback?: boolean;
}
