import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router';
import { PersistGate } from 'redux-persist/es/integration/react';
import 'typeface-open-sans';
import 'typeface-roboto';

import { GAListener } from './components/GAListener/GAListener';
import { initGA } from './ga';
import { NavigationSetter } from './modules/navigation/NavigationSetter';
import { persistor, store } from './modules/store';

import './styles/index.scss';

import App from './routes/App';

initGA();

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <Provider store={store}>
    <PersistGate persistor={persistor}>
      <BrowserRouter>
        <NavigationSetter />
        <GAListener />
        <App />
      </BrowserRouter>
    </PersistGate>
  </Provider>
);
