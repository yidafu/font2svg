import { Tabs, TabList, TabPanels, Tab, TabPanel } from '@chakra-ui/react';
import { PreviewPage } from './pages/PreviewPage';
import { UploadPage } from './pages/UploadPage';
import { FontListPage } from './pages/FontListPage/FontListPage';

import './App.css';

function App() {
  return (
    <div className="w-[1000px] m-auto">
      <Tabs variant="enclosed">
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
  );
}

export default App;
