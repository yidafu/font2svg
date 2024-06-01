
import {
  Table,
  Thead,
  Tbody,
  Tfoot,
  Tr,
  Th,
  Td,
  TableCaption,
  TableContainer,
  Button,
  useToast,
  Tag,
  Stat,
  StatLabel,
  StatNumber,
  StatHelpText,
  StatArrow,
  StatGroup,
  Heading,
  Card, CardHeader, CardBody, CardFooter,
  Link,
  Modal,
  Text,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  useDisclosure,
  Select,
  NumberInput,
  NumberInputField,
} from '@chakra-ui/react'
import { request } from '../utils/request'
import { createContext, useContext, useEffect, useState } from 'react';
import { Font2Svg } from '../components/Font2Fvg';
import { formatFileSize } from '../utils/file';
import { PreviewCode } from '../components/PreviewCode';

interface IPage<T> {
  data: T[]
  total: number;
}

export interface IFontGlyph {
  charText:     string;
  charCode:     number;
  svg: string;
  svgAscender:  number;
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
  tasks: IFontTask[]
}

enum FontTaskStatus {
  Created = "Created",
  Generating = "Generating",
  Done = "Done",
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

export interface IFontGlyph {

}



function getFontList() {
  return request<IFontFace[]>('./fonts/all', { method: 'GET' })
}

function getFonGlyphByPage(faceId: number, page: number = 1, pageSize: number = 20) {
  return request<IPage<IFontGlyph>>(`./fonts/${faceId}/glyphs?page=${page}&size=${pageSize}`, { method: 'GET' })
}

function getFontById(id: number) {
  return request<IFontFace>('./fonts/' + id, { method: 'GET' })
}

function removeFontFace(id: number) {
  return request<IFontFace[]>('./fonts/' + id, { method: 'DELETE' })
}

function BackIcon () {
  return <svg fill="#000000" height="20px" width="20px" version="1.1" 
    viewBox="0 0 477.175 477.175">
    <g>
      <path d="M145.188,238.575l215.5-215.5c5.3-5.3,5.3-13.8,0-19.1s-13.8-5.3-19.1,0l-225.1,225.1c-5.3,5.3-5.3,13.8,0,19.1l225.1,225
		c2.6,2.6,6.1,4,9.5,4s6.9-1.3,9.5-4c5.3-5.3,5.3-13.8,0-19.1L145.188,238.575z"/>
    </g>
  </svg>
}

interface IFontListProps {
  onSelect(id: number): void;
}


function ShowSvgCode(props: { code: string }) {
  const { isOpen, onOpen, onClose } = useDisclosure()
  return(
      <>
      <Button onClick={onOpen}>查看源码</Button>

      <Modal isOpen={isOpen} onClose={onClose} size="6xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>字体SVG源码</ModalHeader>
          <ModalCloseButton />
          <ModalBody>

            <PreviewCode code={props.code.trim()} lang='xml' copyable />
          </ModalBody>

          <ModalFooter>
            <Button colorScheme='blue' mr={3} onClick={onClose}>
              关闭
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
    )
}
function FontStatus(props: { status: FontTaskStatus }) {
  switch (props.status) {
    case FontTaskStatus.Created:
      return <Tag colorScheme='blue'>{props.status}</Tag>
    case FontTaskStatus.Generating:
      return <Tag colorScheme='yellow'>{props.status}</Tag>
    case FontTaskStatus.Done:
      return <Tag colorScheme='green'>{props.status}</Tag>
  }

}

function Pagination(props: { total: number, page: number, size: number, onChange(page: number): void}) {
  const [value, setValue] = useState(props.page)
  const idFirstPage= props.page <= 1;
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

function FontList(props: IFontListProps) {
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
            <Tr>
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


function FontDetail(props: { onBack(): void; fontFaceId: number }) {
  const [fontFace, setFontFace] = useState<Partial<IFontFace>>({})
  const [glyphList, setGlyphList] = useState<IFontGlyph[]>([])
  const [glyphTotal, setGlyphTotal] = useState(0)
  const [page, setPage] = useState(1)
  useEffect(() => {
    if (props.fontFaceId > 0) {
      getFontById(props.fontFaceId).then((list) => {
        setFontFace(list)
        getFonGlyphByPage(props.fontFaceId).then(page => {
          setGlyphList(page.data)
          setGlyphTotal(page.total)
        })
      })
    }
  }, [props.fontFaceId])

  useEffect(() => {
    getFonGlyphByPage(props.fontFaceId, page).then(page => {
      setGlyphList(page.data)
      setGlyphTotal(page.total)
    })
  }, [page, props.fontFaceId])
  return (
    <div>
      <Button leftIcon={<BackIcon />} variant='outline' onClick={props.onBack}>返回</Button>
      <Card>
        <CardHeader>
          <Heading>

            {fontFace.name}
            --
            {fontFace.name ? <Font2Svg
              fontFamily={fontFace.name}
              text={fontFace.previewText}
              fontSize={24}
              color="#333" /> : null}
          </Heading>
          <Link href={fontFace.downloadUrl} download>下载字体</Link>
        </CardHeader>
        <CardBody>
          <StatGroup>
            <Stat>
              <StatLabel>文件大小</StatLabel>
              <StatNumber>{formatFileSize(fontFace.fileSize ?? 0)}</StatNumber>
            </Stat>
            <Stat>
              <StatLabel>字符个数</StatLabel>
              <StatNumber>{(fontFace.glyphCount ?? 0)}个</StatNumber>
            </Stat>
            <Stat>
              <StatLabel>已生成字符个数</StatLabel>
              <StatNumber>{(fontFace?.tasks?.[0]?.generateCount ?? 0)}个</StatNumber>
            </Stat>
          </StatGroup>
        </CardBody>
      </Card>
      <TableContainer className='mt-8'>
        <Table variant='simple'>
          <Thead>
            <Tr>
              <Th>文字</Th>
              <Th isNumeric>CharCode</Th>
              <Th isNumeric>上沿线</Th>
              <Th isNumeric>下沿线</Th>
              <Th>预览</Th>
              <Th>SVG 源码</Th>
            </Tr>
          </Thead>
          <Tbody>
            {glyphList?.map(glyph => {
              return (
                <Tr>
                  <Td >{glyph.charText}</Td>
                  <Td isNumeric>{glyph.charCode}</Td>
                  <Td isNumeric>{glyph.svgAscender}</Td>
                  <Td isNumeric>{glyph.svgDescender}</Td>
                  <Td>
                    <Font2Svg fontFamily={fontFace.name} fontSize={16} text={glyph.charText} color='#999' />
                  </Td>
                  <Td width="300px">
                    <ShowSvgCode code={glyph.svg} />
                  </Td>
                </Tr>
              )
            })}
          </Tbody>
        </Table>
      </TableContainer>
      <Pagination total={glyphTotal} size={20} page={page} onChange={page => setPage(page)}/>
    </div>
  )
}

export function FontListPage() {
  const [font, setFont] = useState({ fontFaceId: -1 })
  return (
    <div>

        {font.fontFaceId < 0 ?
          <FontList onSelect={(id) => setFont({ fontFaceId: id })} />
          : <FontDetail onBack={() => setFont({ fontFaceId: -1})} fontFaceId={font.fontFaceId}/>
        }
    </div>
  )
}