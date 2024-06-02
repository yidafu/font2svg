
import {
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  TableContainer,
  Button,
  useToast,
  Tag
  ,
} from '@chakra-ui/react'
import { useEffect, useState } from 'react';
import { Font2Svg } from '../../../components/Font2Fvg';
import { formatFileSize } from '../../../utils/file';
import { FontTaskStatus, IFontFace, getFontList, removeFontFace } from '../../../api';


function FontStatus(props: { status: FontTaskStatus }) {
  switch (props.status) {
    case FontTaskStatus.Created:
      return <Tag colorScheme='blue'>未开始</Tag>
    case FontTaskStatus.Generating:
      return <Tag colorScheme='yellow'>生成中...</Tag>
    case FontTaskStatus.Done:
      return <Tag colorScheme='green'>已完成</Tag>
  }

}

interface IFontListProps {
  onSelect(id: number): void;
}

export function FontList(props: IFontListProps) {
  const toast = useToast()

  const [fontList, setFontList] = useState<IFontFace[]>()
  async function fetchFontList() {
    const list = await getFontList()
    setFontList(list)
  }
  useEffect(() => {
    fetchFontList()
  }, [])

  async function handleFontRemove(faceId: number) {
    try {
      await removeFontFace(faceId)
      toast({
        title: '删除字体成功',
        description: '字体已从列表中删除',
        status: 'success',
        position: 'top',
        isClosable: true,
      })
      await fetchFontList()
    } catch (e) {
      if (e instanceof Error) {
        toast({
          title: '删除字体失败',
          description: e.message,
          status: 'error',
          position: 'top',
          isClosable: true,
        })
      }
    }
  }
  return <TableContainer>
    <Table variant='simple'>
      <Thead>
        <Tr>
          <Th>字体名称</Th>
          <Th isNumeric>文件大小</Th>
          <Th>状态</Th>
          <Th isNumeric>字数</Th>
          <Th isNumeric>已生成字数</Th>
          <Th>预览</Th>
          <Th>操作</Th>
        </Tr>
      </Thead>
      <Tbody>
        {fontList?.map(fontFace => {
          const task = fontFace.tasks[0]
          return (
            <Tr key={fontFace.id}>
              <Td>{fontFace.name}</Td>
              <Td isNumeric>{formatFileSize(fontFace.fileSize)}</Td>
              <Td ><FontStatus status={task.status} /></Td>
              <Td isNumeric>{fontFace.glyphCount}个</Td>
              <Td isNumeric>{task.generateCount}个</Td>
              <Td >
                <Font2Svg fontFamily={fontFace.name} fontSize={16} text={fontFace.previewText} color='#333' />
              </Td>
              <Td>
                <Button variant='ghost' size='sm' onClick={() => props.onSelect(fontFace.id)}>详情</Button>
                <Button onClick={() => handleFontRemove(fontFace.id)} variant='ghost' size='sm' colorScheme='red'>删除</Button>
              </Td>
            </Tr>
          )
        })}
      </Tbody>
    </Table>
  </TableContainer>
}