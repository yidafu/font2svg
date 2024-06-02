import {
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  TableContainer,
  Button,
  Stat,
  StatLabel,
  StatNumber,
  StatGroup,
  Heading,
  Card, CardHeader, CardBody,
  Link,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  useDisclosure,
} from '@chakra-ui/react'
import { useEffect, useState } from 'react';
import { Font2Svg } from '../../../components/Font2Fvg';
import { formatFileSize } from '../../../utils/file';
import { IFontFace, IFontGlyph, getFonGlyphByPage, getFontById } from '../../../api';
import { PreviewCode } from '../../../components/PreviewCode';
import { Pagination } from '../../../components/Pagination';


function BackIcon() {
  return <svg fill="#000000" height="20px" width="20px" version="1.1"
    viewBox="0 0 477.175 477.175">
    <g>
      <path d="M145.188,238.575l215.5-215.5c5.3-5.3,5.3-13.8,0-19.1s-13.8-5.3-19.1,0l-225.1,225.1c-5.3,5.3-5.3,13.8,0,19.1l225.1,225
		c2.6,2.6,6.1,4,9.5,4s6.9-1.3,9.5-4c5.3-5.3,5.3-13.8,0-19.1L145.188,238.575z"/>
    </g>
  </svg>
}


function ShowSvgCode(props: { code: string }) {
  const { isOpen, onOpen, onClose } = useDisclosure()
  return (
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



export  function FontDetail(props: { onBack(): void; fontFaceId: number }) {
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
      <Pagination total={glyphTotal} size={20} page={page} onChange={page => setPage(page)} />
    </div>
  )
}
