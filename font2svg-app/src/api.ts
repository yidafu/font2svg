import { request } from './utils';

export interface IFontTask {
  fontUrl: string;
  fontFamily: string;
  previewText: string;
}
export interface IUploadResult {
  filename: string;
  url: string;
  size: number;
}

export async function uploadFileRequest(file: File) {
  const formData = new FormData();
  formData.append('file-uploads', file);

  const data = await request<IUploadResult>('/files/upload', {
    method: 'POST',
    body: formData,
  });
  return data;
}


export function createTask(task: IFontTask) {
  return request('/tasks/', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(task),
  });
}

interface IPage<T> {
  data: T[];
  total: number;
}

export interface IFontGlyph {
  charText: string;
  charCode: number;
  svg: string;
  svgAscender: number;
  svgDescender: number;
}

export interface IFontFace {
  id: number;
  createdAt: number;
  updatedAt: number;
  name: string;
  glyphCount: number;
  fileSize: number;
  previewText: string;
  downloadUrl: string;
  tasks: IFontTask[];
}

export enum FontTaskStatus {
  Created = 'Created',
  Generating = 'Generating',
  Done = 'Done',
}

export interface IFontTask {
  id: number;
  createdAt: number;
  updatedAt: number;
  fontFamily: string;
  fileSize: number;
  tobalCount: number;
  generateCount: number;
  tempFilepath: string;
  status: FontTaskStatus;
  fontFaceId: number;
}

export function getFontList() {
  return request<IFontFace[]>('/fonts/all', { method: 'GET' });
}

export function getFonGlyphByPage(
  faceId: number,
  page: number = 1,
  pageSize: number = 20
) {
  return request<IPage<IFontGlyph>>(
    `./fonts/${faceId}/glyphs?page=${page}&size=${pageSize}`,
    { method: 'GET' }
  );
}

export function getFontById(id: number) {
  return request<IFontFace>('/fonts/' + id, { method: 'GET' });
}

export function removeFontFace(id: number) {
  return request<IFontFace[]>('/fonts/' + id, { method: 'DELETE' });
}
