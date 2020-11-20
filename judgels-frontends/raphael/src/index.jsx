import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/es/integration/react';
import { ConnectedRouter } from 'connected-react-router';

import './styles/index.css';

import { initGA } from './ga';
import App from './routes/App';
import { history, persistor, store } from './modules/store';

require('typeface-open-sans');
require('typeface-roboto');

// https://github.com/yahoo/react-intl/issues/465#issuecomment-369566628
const consoleError = console.error.bind(console);
console.error = (message, ...args) => {
  if (typeof message === 'string' && message.startsWith('[React Intl] Missing message:')) {
    return;
  }
  consoleError(message, ...args);
};

initGA(history);

ReactDOM.render(
  <Provider store={store}>
    <PersistGate persistor={persistor}>
      <ConnectedRouter history={history}>
        <App />
      </ConnectedRouter>
    </PersistGate>
  </Provider>,
  document.getElementById('root')
);
