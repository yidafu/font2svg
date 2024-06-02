import { SyntheticEvent, forwardRef, useEffect, useRef, useState } from 'react';
import cn from 'classname';
import { request } from '../utils/request';
import { FormErrorMessage, useToast } from '@chakra-ui/react';
import { UseFormRegister, UseFormRegisterReturn } from 'react-hook-form';

export interface IUploadResult {
  filename: string;
  url: string;
  size: number;
}

async function uploadFileRequest(file: File) {
  const formData = new FormData();
  formData.append('file-uploads', file);

  const data = await request<IUploadResult>('./files/upload', {
    method: 'POST',
    body: formData,
  });
  return data;
}

export interface IUploadFontFileProps {}

export const UploadFontFile = forwardRef<HTMLDivElement>(function (
  props: IUploadFontFileProps & UseFormRegisterReturn<'fontUrl'>,
  uploadRef
) {
  const fileRef = useRef<HTMLInputElement | null>(null);
  const [dragging, setDragging] = useState(false);
  const toast = useToast();
  async function handleUploadFile(files: FileList) {
    if (files.length > 0) {
      const fontFile = files.item(0);
      if (fontFile) {
        try {
          const res = await uploadFileRequest(fontFile);
          const evt = {
            target: { value: res.url, name: props.name },
            type: 'change',
          };
          console.log('event', evt);
          props.onChange(evt);
        } catch (e) {
          if (e instanceof Error) {
            toast({
              title: '上传失败',
              description: e.message,
              status: 'error',
              position: 'top',
              isClosable: true,
            });
            return;
          }
        }
      }

      if (fileRef.current) {
        fileRef.current.files = files;
      }
    }
  }
  const handleDrop = (evt: React.DragEvent<HTMLDivElement>) => {
    evt.preventDefault();
    const files = evt.dataTransfer?.files;
    if (!files) {
      return;
    }
    handleUploadFile(files);
  };

  const handleFileChange = (evt: React.ChangeEvent<HTMLInputElement>) => {
    console.log('input event', evt);
    const files = evt.target.files;
    if (!files) {
      return;
    }
    handleUploadFile(files);
  };

  return (
    <div
      ref={uploadRef}
      className={cn('drop-container', { 'drag-active': dragging })}
      id="dropcontainer"
      onDragOver={(evt) => {
        evt.preventDefault();
      }}
      onDragEnter={() => setDragging(true)}
      onDragLeave={() => setDragging(false)}
      onDrop={handleDrop}
    >
      <span className="drop-title">拖拽字体文件到此</span>
      或者
      <input
        ref={fileRef}
        type="file"
        id="fonts"
        accept="font/*"
        required
        onChange={handleFileChange}
      />
    </div>
  );
});
