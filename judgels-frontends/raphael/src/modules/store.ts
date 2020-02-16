import { createBrowserHistory } from 'history';
import { connectRouter, routerMiddleware, RouterState } from 'connected-react-router';
import { applyMiddleware, combineReducers, compose, createStore } from 'redux';
import { FormState, reducer as formReducer } from 'redux-form';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';
import thunk from 'redux-thunk';

import { urielAdminAPI } from './api/uriel/admin';
import { contestAPI } from './api/uriel/contest';
import { contestWebAPI } from './api/uriel/contestWeb';
import { contestAnnouncementAPI } from './api/uriel/contestAnnouncement';
import { contestClarificationAPI } from './api/uriel/contestClarification';
import { contestContestantAPI } from './api/uriel/contestContestant';
import { contestHistoryAPI } from './api/uriel/contestHistory';
import { contestSupervisorAPI } from './api/uriel/contestSupervisor';
import { contestManagerAPI } from './api/uriel/contestManager';
import { contestModuleAPI } from './api/uriel/contestModule';
import { contestProblemAPI } from './api/uriel/contestProblem';
import { contestRatingAPI } from './api/uriel/contestRating';
import { contestScoreboardAPI } from './api/uriel/contestScoreboard';
import { contestSubmissionProgrammingAPI } from './api/uriel/contestSubmissionProgramming';
import { contestSubmissionBundleAPI } from './api/uriel/contestSubmissionBundle';
import { contestFileAPI } from './api/uriel/contestFile';
import { sessionReducer, SessionState } from './session/sessionReducer';
import { webPrefsReducer, WebPrefsState } from './webPrefs/webPrefsReducer';
import { toastActions } from './toast/toastActions';
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
  composeEnhancers(
    applyMiddleware(
      toastMiddleware,
      tokenGateMiddleware,
      thunk.withExtraArgument({
        urielAdminAPI,
        contestAPI,
        contestWebAPI,
        contestAnnouncementAPI,
        contestClarificationAPI,
        contestContestantAPI,
        contestHistoryAPI,
        contestSupervisorAPI,
        contestManagerAPI,
        contestModuleAPI,
        contestProblemAPI,
        contestRatingAPI,
        contestScoreboardAPI,
        contestSubmissionBundleAPI,
        contestSubmissionProgrammingAPI,
        contestFileAPI,
        toastActions,
      }),
      routerMiddleware(history)
    )
  )
);

export const persistor = persistStore(store);
