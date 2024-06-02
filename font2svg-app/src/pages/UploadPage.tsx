import { Button, Input, useToast } from '@chakra-ui/react';
import { useForm, SubmitHandler } from 'react-hook-form';

import { FormRow } from '../components/FormRow';
import { UploadFontFile } from '../components/UploadFontFile';
import { IFontTask, createTask } from '../api';

export interface IFontStyle {
  fontSize: number;
  color: string;
  fontFamily: string;
}

export function UploadPage() {
  const toast = useToast();
  const { register, handleSubmit, reset } = useForm<IFontTask>();
  const onSubmit: SubmitHandler<IFontTask> = async (data) => {
    try {
      await createTask(data);
      reset();
      toast({
        title: '提交成功',
        description: '后台正常生成 SVG ...',
        status: 'success',
        position: 'top',
        isClosable: true,
      });
    } catch (e) {
      if (e instanceof Error) {
        toast({
          title: '提交失败',
          description: e.message,
          status: 'error',
          position: 'top',
          isClosable: true,
        });
      }
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit(onSubmit)}>
        <FormRow label="上传字体源文件">
          <UploadFontFile {...register('fontUrl')} />
        </FormRow>

        <FormRow label="字体名称" placeholder="请填写字体名称">
          <Input {...register('fontFamily', { required: true })} />
        </FormRow>

        <FormRow label="预览文本" placeholder="请填写预览文本">
          <Input {...register('previewText', { required: true })} />
        </FormRow>

        <div className="flex justify-center ">
          <Input
            type="submit"
            className="chakra-button css-ez23ye"
            value="提交"
            width="auto"
          />
        </div>
        <Button hidden>提交</Button>
      </form>
    </div>
  );
}
