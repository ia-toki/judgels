import { RouterProvider } from '@tanstack/react-router';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/es/integration/react';
import 'typeface-open-sans';
import 'typeface-roboto';

import { initGA } from './ga';
import { persistor, store } from './modules/store';
import { router } from './routes/router';

import './styles/index.scss';

initGA();

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <Provider store={store}>
    <PersistGate persistor={persistor}>
      <RouterProvider router={router} />
    </PersistGate>
  </Provider>
);
