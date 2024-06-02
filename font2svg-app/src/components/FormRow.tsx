import { PropsWithChildren } from 'react'

import {
  FormControl,
  FormLabel,
  FormHelperText,
} from '@chakra-ui/react'



export function FormRow(props: PropsWithChildren<{ label: string, placeholder?: string, message?: string }>) {
  return <FormControl className='flex m-4'>
    <FormLabel className="inline min-w-24" display='inline' >{props.label}:</FormLabel>
    <div className='flex flex-col grow'>
      {props.children}
      {props.message ? <span className='text-rose-600'>{props.message}</span> : null}
      {props.placeholder ? <FormHelperText>{props.placeholder}</FormHelperText> : null}
    </div>
  </FormControl>
}
