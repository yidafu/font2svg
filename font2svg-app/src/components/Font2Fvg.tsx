import { createComponent, createDynamicComponent } from 'font2svg-react';

export const SvgText = createComponent({
  assertUrl: 'http://127.0.0.1:8888/asserts',
});
export const DynamicSvgText = createDynamicComponent({
  assertUrl: 'http://127.0.0.1:8888/asserts',
});
