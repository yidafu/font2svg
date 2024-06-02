import React from 'react';
import ReactDOM from 'react-dom/client';

import {
  ChakraProvider,
  extendTheme,
  withDefaultColorScheme,
} from '@chakra-ui/react';

import './index.css';
import App from './App';

const customTheme = extendTheme(
  withDefaultColorScheme({
    colorScheme: 'blue',
    components: {
      FormLabel: {
        baseStyle: {
          display: 'inline',
        },
      },
    },
  })
);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ChakraProvider theme={customTheme}>
      <App />
    </ChakraProvider>
  </React.StrictMode>
);
