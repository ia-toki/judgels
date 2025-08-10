import { ConnectedRouter } from 'connected-react-router';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/es/integration/react';
import 'typeface-open-sans';
import 'typeface-roboto';

import { initGA } from './ga';
import { history, persistor, store } from './modules/store';

import './styles/index.scss';

import App from './routes/App';

initGA(history);

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <Provider store={store}>
    <PersistGate persistor={persistor}>
      <ConnectedRouter history={history}>
        <App />
      </ConnectedRouter>
    </PersistGate>
  </Provider>
);
