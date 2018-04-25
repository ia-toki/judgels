import createBrowserHistory from 'history/createBrowserHistory';
import { routerMiddleware, routerReducer, RouterState } from 'react-router-redux';
import { applyMiddleware, combineReducers, compose, createStore } from 'redux';
import { FormState, reducer as formReducer } from 'redux-form';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';
import thunk from 'redux-thunk';

import { createSessionAPI } from './api/jophiel/session';
import { createLegacySessionAPI } from './api/jophiel/legacySession';
import { createMyAPI } from './api/jophiel/my';
import { createUserAPI } from './api/jophiel/user';
import { createUserAccountAPI } from './api/jophiel/userAccount';
import { createUserProfileAPI } from './api/jophiel/userProfile';
import { createWebAPI } from './api/jophiel/web';
import { createContestAPI } from './api/uriel/contest';
import { createContestAnnouncementAPI } from './api/uriel/contestAnnouncement';
import { createContestScoreboardAPI } from './api/uriel/contestScoreboard';
import { sessionReducer, SessionState } from './session/sessionReducer';
import { toastActions } from './toast/toastActions';
import { toastMiddleware } from './toast/toastMiddleware';
import { tokenGateMiddleware } from './tokenGate/tokenGateMiddleware';
import { jophielReducer, JophielState } from '../routes/jophiel/modules/jophielReducer';
import { urielReducer, UrielState } from '../routes/uriel/competition/modules/urielReducer';
import { breadcrumbsReducer, BreadcrumbsState } from './breadcrumbs/breadcrumbsReducer';

export interface AppState {
  session: SessionState;
  jophiel: JophielState;
  uriel: UrielState;
  router: RouterState;
  form: FormState;
  breadcrumbs: BreadcrumbsState;
}

const rootReducer = combineReducers<AppState>({
  session: persistReducer({ key: 'session', storage }, sessionReducer),
  jophiel: jophielReducer,
  uriel: urielReducer,
  router: routerReducer,
  form: formReducer,
  breadcrumbs: breadcrumbsReducer,
});

const composeEnhancers = (window as any).__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export const history = createBrowserHistory();

export const store = createStore<AppState>(
  rootReducer,
  composeEnhancers(
    applyMiddleware(
      toastMiddleware,
      tokenGateMiddleware,
      thunk.withExtraArgument({
        sessionAPI: createSessionAPI(),
        legacySessionAPI: createLegacySessionAPI(),
        myAPI: createMyAPI(),
        webAPI: createWebAPI(),
        userAPI: createUserAPI(),
        userAccountAPI: createUserAccountAPI(),
        userProfileAPI: createUserProfileAPI(),
        contestAPI: createContestAPI(),
        contestAnnouncementAPI: createContestAnnouncementAPI(),
        contestScoreboardAPI: createContestScoreboardAPI(),
        toastActions,
      }),
      routerMiddleware(history)
    )
  )
);

export const persistor = persistStore(store);
