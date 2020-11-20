import { createBrowserHistory } from 'history';
import { connectRouter, routerMiddleware } from 'connected-react-router';
import { applyMiddleware, combineReducers, compose, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';
import thunk from 'redux-thunk';

import sessionReducer from './session/sessionReducer';
import webPrefsReducer from './webPrefs/webPrefsReducer';
import toastMiddleware from './toast/toastMiddleware';
import tokenGateMiddleware from './tokenGate/tokenGateMiddleware';
import jophielReducer from './jophiel/jophielReducer';
import urielReducer from './uriel/urielReducer';
import jerahmeelReducer from './jerahmeel/jerahmeelReducer';
import breadcrumbsReducer from './breadcrumbs/breadcrumbsReducer';

export const history = createBrowserHistory();

const rootReducer = combineReducers({
  session: persistReducer({ key: 'session', storage }, sessionReducer),
  webPrefs: persistReducer({ key: 'webPrefs', storage }, webPrefsReducer),
  jophiel: jophielReducer,
  uriel: urielReducer,
  jerahmeel: jerahmeelReducer,
  router: connectRouter(history),
  form: formReducer,
  breadcrumbs: breadcrumbsReducer,
});

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export const store = createStore(
  rootReducer,
  composeEnhancers(applyMiddleware(toastMiddleware, tokenGateMiddleware, thunk, routerMiddleware(history)))
);

export const persistor = persistStore(store);
