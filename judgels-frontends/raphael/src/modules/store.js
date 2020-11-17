import { createBrowserHistory } from 'history';
import { connectRouter, routerMiddleware, RouterState } from 'connected-react-router';
import { applyMiddleware, combineReducers, compose, createStore } from 'redux';
import { FormState, reducer as formReducer } from 'redux-form';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';
import thunk from 'redux-thunk';

import { sessionReducer, SessionState } from './session/sessionReducer';
import { webPrefsReducer, WebPrefsState } from './webPrefs/webPrefsReducer';
import { toastMiddleware } from './toast/toastMiddleware';
import { tokenGateMiddleware } from './tokenGate/tokenGateMiddleware';
import { jophielReducer, JophielState } from './jophiel/jophielReducer';
import { urielReducer, UrielState } from './uriel/urielReducer';
import { jerahmeelReducer, JerahmeelState } from './jerahmeel/jerahmeelReducer';
import { breadcrumbsReducer, BreadcrumbsState } from './breadcrumbs/breadcrumbsReducer';

export interface AppState {
  session: SessionState;
  webPrefs: WebPrefsState;
  jophiel: JophielState;
  uriel: UrielState;
  jerahmeel: JerahmeelState;
  router: RouterState;
  form: FormState;
  breadcrumbs: BreadcrumbsState;
}

export const history = createBrowserHistory();

const rootReducer = combineReducers<AppState>({
  session: persistReducer({ key: 'session', storage }, sessionReducer),
  webPrefs: persistReducer({ key: 'webPrefs', storage }, webPrefsReducer),
  jophiel: jophielReducer,
  uriel: urielReducer,
  jerahmeel: jerahmeelReducer,
  router: connectRouter(history),
  form: formReducer,
  breadcrumbs: breadcrumbsReducer,
});

const composeEnhancers = (window as any).__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export const store: any = createStore<AppState>(
  rootReducer,
  composeEnhancers(applyMiddleware(toastMiddleware, tokenGateMiddleware, thunk, routerMiddleware(history)))
);

export const persistor = persistStore(store);
