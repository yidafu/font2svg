import { createComponent, createDynamicComponent } from 'font2svg-react';

export const Font2Svg = createComponent({
  assertUrl: 'http://127.0.0.1:8888/asserts',
});
export const DynamicFont2Svg = createDynamicComponent({
  assertUrl: 'http://127.0.0.1:8888/asserts',
});
