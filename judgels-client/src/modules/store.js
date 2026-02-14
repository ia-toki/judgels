import { applyMiddleware, combineReducers, compose, createStore } from 'redux';
import { persistReducer, persistStore } from 'redux-persist';
import storage from 'redux-persist/es/storage';
import thunk from 'redux-thunk';

import sessionReducer from './session/sessionReducer';
import toastMiddleware from './toast/toastMiddleware';
import tokenGateMiddleware from './tokenGate/tokenGateMiddleware';

const rootReducer = combineReducers({
  session: persistReducer({ key: 'session', storage }, sessionReducer),
});

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export const store = createStore(
  rootReducer,
  composeEnhancers(applyMiddleware(toastMiddleware, tokenGateMiddleware, thunk))
);

export const persistor = persistStore(store);
