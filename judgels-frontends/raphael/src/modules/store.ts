import createBrowserHistory from 'history/createBrowserHistory';
import { routerMiddleware, routerReducer, RouterState } from 'react-router-redux';
import { applyMiddleware, combineReducers, compose, createStore } from 'redux';
import { FormState, reducer as formReducer } from 'redux-form';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';
import thunk from 'redux-thunk';

import { sessionAPI } from './api/jophiel/session';
import { legacySessionAPI } from './api/jophiel/legacySession';
import { profileAPI } from './api/jophiel/profile';
import { userAPI } from './api/jophiel/user';
import { myUserAPI } from './api/jophiel/myUser';
import { userAccountAPI } from './api/jophiel/userAccount';
import { userAvatarAPI } from './api/jophiel/userAvatar';
import { userInfoAPI } from './api/jophiel/userInfo';
import { userRegistrationWebAPI } from './api/jophiel/userRegistration';
import { userSearchAPI } from './api/jophiel/userSearch';
import { userWebAPI } from './api/jophiel/userWeb';
import { urielAdminAPI } from './api/uriel/admin';
import { contestAPI } from './api/uriel/contest';
import { contestWebAPI } from './api/uriel/contestWeb';
import { contestAnnouncementAPI } from './api/uriel/contestAnnouncement';
import { contestClarificationAPI } from './api/uriel/contestClarification';
import { contestContestantAPI } from './api/uriel/contestContestant';
import { contestSupervisorAPI } from './api/uriel/contestSupervisor';
import { contestManagerAPI } from './api/uriel/contestManager';
import { contestModuleAPI } from './api/uriel/contestModule';
import { contestProblemAPI } from './api/uriel/contestProblem';
import { contestScoreboardAPI } from './api/uriel/contestScoreboard';
import { contestProgrammingSubmissionAPI } from './api/uriel/contestProgrammingSubmission';
import { contestFileAPI } from './api/uriel/contestFile';
import { sessionReducer, SessionState } from './session/sessionReducer';
import { webPrefsReducer, WebPrefsState } from './webPrefs/webPrefsReducer';
import { toastActions } from './toast/toastActions';
import { toastMiddleware } from './toast/toastMiddleware';
import { tokenGateMiddleware } from './tokenGate/tokenGateMiddleware';
import { jophielReducer, JophielState } from '../routes/jophiel/modules/jophielReducer';
import { urielReducer, UrielState } from '../routes/uriel/modules/urielReducer';
import { breadcrumbsReducer, BreadcrumbsState } from './breadcrumbs/breadcrumbsReducer';

export interface AppState {
  session: SessionState;
  webPrefs: WebPrefsState;
  jophiel: JophielState;
  uriel: UrielState;
  router: RouterState;
  form: FormState;
  breadcrumbs: BreadcrumbsState;
}

const rootReducer = combineReducers<AppState>({
  session: persistReducer({ key: 'session', storage }, sessionReducer),
  webPrefs: persistReducer({ key: 'webPrefs', storage }, webPrefsReducer),
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
        sessionAPI,
        legacySessionAPI,
        profileAPI,
        userAPI,
        myUserAPI,
        userAccountAPI,
        userAvatarAPI,
        userInfoAPI,
        userRegistrationWebAPI,
        userSearchAPI,
        userWebAPI,
        urielAdminAPI,
        contestAPI,
        contestWebAPI,
        contestAnnouncementAPI,
        contestClarificationAPI,
        contestContestantAPI,
        contestSupervisorAPI,
        contestManagerAPI,
        contestModuleAPI,
        contestProblemAPI,
        contestScoreboardAPI,
        contestProgrammingSubmissionAPI,
        contestFileAPI,
        toastActions,
      }),
      routerMiddleware(history)
    )
  )
);

export const persistor = persistStore(store);
