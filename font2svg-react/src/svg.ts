export interface IElement extends INode {
  tagName: string;
  children: ISvgElement[];
}

export interface IText extends INode {
  value: string;
  tagName: 'text';
}

export interface INode {
  tagName: string;
  properties: Record<string, string>;
}

export type ISvgElement = IElement | IText;

const CHAR_A = 65;
const CHAT_Z = 90;
const CHAT_a = 97;
const CHAT_z = 122;

const CHAT_0 = 48;
const CHAT_9 = 57;

export function parse(source: string): IElement {
  let index = 0;
  let char = source[index];
  const root = {
    tagName: 'root',
    properties: {},
    children: [],
  };

  const stack: IElement[] = [];

  let currentElement: IElement = root;

  function peek(offset: number = 0) {
    return source[index + offset];
  }

  function next() {
    char = source[++index];
  }

  function eof() {
    return index >= source.length;
  }

  function validChar(char: string) {
    const code = char.charCodeAt(0);
    return (
      (code >= CHAR_A && code <= CHAT_Z) ||
      (code >= CHAT_a && code <= CHAT_z) ||
      (code >= CHAT_0 && code <= CHAT_9) ||
      char === '-'
    );
  }

  function isWhitespace() {
    return char === ' ' || char === '\n' || char === '\r' || char === '\t';
  }

  function skipWhiteSpace() {
    while (!eof() && isWhitespace()) {
      next();
    }
  }

  function string() {
    let str = '';
    while (!eof() && validChar(char)) {
      str += char;
      next();
    }
    return str;
  }

  function neutral() {
    let text = '';
    if (!eof() && char !== '<') {
      text += char;
      next();
    }
    if (text.length > 0) {
      const textNode: IText = {
        tagName: 'text',
        properties: {},
        value: text,
      };
      currentElement.children.push(textNode);
    }
    if (char === '<') {
      tag();
    }
  }

  function quoteString() {
    let value = '';
    if (char === '"' || char === "'") {
      const startQuote = char;
      next(); // skip quote
      while (!eof() && char !== startQuote) {
        value += char;
        next();
      }
      next(); // skip quote
    }
    return value;
  }

  function comment() {
    next(); // skip !
    next(); // skip -
    next(); // skip -

    while (!eof()) {
      if (char === '-') {
        if (peek(1) === '-' && peek(2) === '>') {
          next(); // skip -
          next(); // skip -
          next(); // skip >
          break;
        }
      }
      next();
    }
  }

  function doctype() {
    while (!eof() && char === '>') {
      next();
    }
    next(); // skip >
  }

  function header() {
    next(); // skip ?
    while (!eof()) {
      if (char === '?' && peek(1) === '>') {
        next(); // skip ?
        next(); // skip >
        break;
      }
      next();
    }
  }

  function getAttributes() {
    let attrs: Record<string, string> = {};
    skipWhiteSpace();
    while (!eof() && char !== '>' && char !== '/') {
      skipWhiteSpace();
      const attr = string();
      skipWhiteSpace();
      if (char === '=') {
        next();
        skipWhiteSpace();
        const value = quoteString();
        skipWhiteSpace();
        attrs[attr] = value;
      } else {
        attrs[attr] = 'true';
      }
    }
    return attrs;
  }

  function close() {
    next(); // skip /
    const name = string();
    if (currentElement.tagName !== name) {
      throw SyntaxError(`Expect close tag ${currentElement.tagName}`);
    }
    skipWhiteSpace();
    next(); // skip >
    stack.pop();
    currentElement = stack[stack.length - 1];
  }

  function tag() {
    next();
    if (char === '?') {
      header();
      return;
    }

    if (char === '!') {
      if (peek(1) === '-' && peek(2) === '-') {
        comment();
        return;
      }
      if (peek(1) === 'd') {
        if (
          peek(2) === 'o' &&
          peek(3) === 'c' &&
          peek(4) === 't' &&
          peek(5) === 'y' &&
          peek(6) === 'p' &&
          peek(7) === 'e'
        ) {
          doctype();
          return;
        }
      }
      if (peek(1) === '[') {
        // TODO: skip cdata
      }
    }

    if (char === '/') {
      close();
      return;
    }

    const name = string();

    const attrs = getAttributes();
    const element: IElement = {
      tagName: name,
      properties: attrs,
      children: [],
    };

    currentElement.children.push(element);

    let selfClose = false;

    if (char === '/') {
      selfClose = true;
      next();
    }
    if (!selfClose) {
      stack.push(element);
      currentElement = element;
    }
    if (char !== '>') {
      throw SyntaxError('Expect ">"');
    }
    next(); // skip >
  }

  while (!eof()) {
    skipWhiteSpace();
    neutral();
  }

  return root;
}

export function stringify(root: IElement) {
  function buildSvgNode(node: ISvgElement) {
    if (node.tagName === 'text') {
      return (node as IText).value;
    }
    const element = node as IElement;
    let segment = `<${element.tagName} ${Object.entries(element.properties)
      .map(([attr, value]) => `${attr}="${value}"`)
      .join(' ')}`;

    const hasChildren = element.children.length > 0;
    if (hasChildren) {
      segment += '>';
      segment += element.children.map(buildSvgNode).join('');
      segment += `</${element.tagName}>`;
    } else {
      segment += '/>';
    }
    return segment;
  }

  return root.children.map(buildSvgNode).join('');
}

export function isText(node: ISvgElement): node is IText {
  return node.tagName === 'text';
}

export function isElement(node: ISvgElement): node is IElement {
  return node.tagName !== 'text';
}
