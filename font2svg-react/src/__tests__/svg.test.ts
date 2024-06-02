import { parse, stringify, IElement } from '../svg';

describe('Svg', () => {
  it('parse svg', () => {
    const root = parse(`<?xml version="1.0" encoding="UTF-8" ?>
<svg height="19.2px" viewBox="0 -954 960 1200" xmlns="http://www.w3.org/2000/svg">
<path d="M 84 -569" fill="currentColor"/>
</svg>`);

    expect(root.children.length).toBe(1);
    expect(root.children[0].tagName).toBe('svg');
    expect(root.children[0].children[0].tagName).toBe('path');
  });

  it('parse svg with comment', () => {
    const root = parse(`<svg height="19.2px"><!-- comment --></svg>`);

    expect(root.children.length).toBe(1);
    expect(root.children[0].tagName).toBe('svg');
  });

  it('parse svg with dataset', () => {
    const root = parse(
      `<svg height="19.2px" data-id="1234"><!-- comment --></svg>`
    );

    expect(root.children.length).toBe(1);
    expect(root.children[0].tagName).toBe('svg');
  });

  it('stringify svg tree object', () => {
    const root: IElement = {
      tagName: 'root',
      properties: {},
      children: [
        {
          tagName: 'svg',
          properties: { foo: 'bar' },
          children: [
            {
              tagName: 'path',
              properties: { d: 'M 10 10' },
              children: [],
            },
          ],
        },
      ],
    };

    const svg = stringify(root);
    expect(svg).toBe(`<svg foo="bar"><path d="M 10 10"/></svg>`);
  });
});
