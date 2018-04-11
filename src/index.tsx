import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/es/integration/react';
import { ConnectedRouter } from 'react-router-redux';

import './styles/index.css';

import App from './routes/App';
import { history, persistor, store } from './modules/store';

// https://github.com/yahoo/react-intl/issues/465#issuecomment-369566628
const consoleError = console.error.bind(console);
console.error = (message, ...args) => {
  if (typeof message === 'string' && message.startsWith('[React Intl] Missing message:')) {
    return;
  }
  consoleError(message, ...args);
};

ReactDOM.render(
  <Provider store={store}>
    <PersistGate persistor={persistor}>
      <ConnectedRouter history={history}>
        <App />
      </ConnectedRouter>
    </PersistGate>
  </Provider>,
  document.getElementById('root') as HTMLElement
);
