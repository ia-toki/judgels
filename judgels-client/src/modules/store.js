import { applyMiddleware, combineReducers, compose, createStore } from 'redux';
import { persistReducer, persistStore } from 'redux-persist';
import storage from 'redux-persist/es/storage';
import thunk from 'redux-thunk';

import jophielReducer from './jophiel/jophielReducer';
import sessionReducer from './session/sessionReducer';
import toastMiddleware from './toast/toastMiddleware';
import tokenGateMiddleware from './tokenGate/tokenGateMiddleware';
import webPrefsReducer from './webPrefs/webPrefsReducer';

const rootReducer = combineReducers({
  session: persistReducer({ key: 'session', storage }, sessionReducer),
  webPrefs: persistReducer({ key: 'webPrefs', storage }, webPrefsReducer),
  jophiel: jophielReducer,
});

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export const store = createStore(
  rootReducer,
  composeEnhancers(applyMiddleware(toastMiddleware, tokenGateMiddleware, thunk))
);

export const persistor = persistStore(store);
