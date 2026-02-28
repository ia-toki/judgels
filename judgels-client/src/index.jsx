import { createAsyncStoragePersister } from '@tanstack/query-async-storage-persister';
import { PersistQueryClientProvider } from '@tanstack/react-query-persist-client';
import { RouterProvider } from '@tanstack/react-router';
import { createRoot } from 'react-dom/client';
import 'typeface-open-sans';
import 'typeface-roboto';

import { initGA } from './ga';
import { queryClient } from './modules/queryClient';
import { WebPrefsProvider } from './modules/webPrefs';
import { router } from './routes/router';

import './styles/index.scss';

initGA();

const persister = createAsyncStoragePersister({
  storage: window.localStorage,
});

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <PersistQueryClientProvider client={queryClient} persistOptions={{ persister, buster: '3' }}>
    <WebPrefsProvider>
      <RouterProvider router={router} />
    </WebPrefsProvider>
  </PersistQueryClientProvider>
);
