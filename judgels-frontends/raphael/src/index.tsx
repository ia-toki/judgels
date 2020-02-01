import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as ReactGA from 'react-ga';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/es/integration/react';
import { ConnectedRouter } from 'connected-react-router';

import './styles/index.css';

import App from './routes/App';
import { history, persistor, store } from './modules/store';
import { APP_CONFIG } from './conf';

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

if (APP_CONFIG.googleAnalytics) {
  ReactGA.initialize(APP_CONFIG.googleAnalytics.trackingId);
  history.listen(location => {
    ReactGA.set({ page: location.pathname + location.search });
    ReactGA.pageview(location.pathname + location.search);
  });
}

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
