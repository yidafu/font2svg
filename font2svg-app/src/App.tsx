import './App.css'
import * as React from 'react'
import { Outlet } from 'react-router-dom'
import { Tabs, TabList, TabPanels, Tab, TabPanel } from '@chakra-ui/react'
// import { createComponent, createDynamicComponent } from './font2svg'
// const Font2Svg = createComponent({ assertUrl: 'http://127.0.0.1:8888/assets' })
// const Font2Svg2 = createDynamicComponent({ assertUrl: 'http://127.0.0.1:8888/assets' })
import { Button, ButtonGroup } from '@chakra-ui/react'
import { PreviewPage } from './pages/PreviewPage'
import { UploadPage } from './pages/UploadPage'
import { FontListPage } from './pages/FontListPage/FontListPage'

function App() {

  return (
    <div className='w-[1000px] m-auto'>
      <Tabs variant='enclosed'>
        <TabList>
          <Tab>预览</Tab>
          <Tab>上传字体</Tab>
          <Tab>字体列表</Tab>
        </TabList>
        <TabPanels>
          <TabPanel>
            <PreviewPage />
          </TabPanel>
          <TabPanel>
            <UploadPage />
          </TabPanel>

          <TabPanel>
            <FontListPage />
          </TabPanel>
        </TabPanels>
      </Tabs>
    </div>
  )
}

export default App
