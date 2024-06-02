
import {
  Button,
  Text,
  NumberInput,
  NumberInputField,
} from '@chakra-ui/react'
import { useEffect, useState } from 'react';





export function Pagination(props: { total: number, page: number, size: number, onChange(page: number): void }) {
  const [value, setValue] = useState(props.page)
  const idFirstPage = props.page <= 1;
  const lastPage = Math.ceil(props.total / props.size)
  const idLastPage = props.page >= lastPage

  useEffect(() => {
    setValue(props.page)
  }, [props.page])
  return <div className='flex justify-end my-4 items-center'>
    <Text fontSize="md" className='mr-4'>总共{lastPage}页</Text>
    <Button disabled={idFirstPage} className='mr-4' onClick={() => props.onChange(props.page + 1)}>下一页</Button>
    <Button disabled={idLastPage} className='mr-4' onClick={() => props.onChange(props.page - 1)}>上一页</Button>

    <NumberInput value={value} onChange={(v) => setValue(parseInt(v, 10))} className='w-24'>
      <NumberInputField />
    </NumberInput>
    <Button onClick={() => props.onChange(value)}>跳到</Button>

  </div>
}