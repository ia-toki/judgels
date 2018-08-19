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
import { createProfileAPI } from './api/jophiel/profile';
import { createUserAPI } from './api/jophiel/user';
import { createUserAccountAPI } from './api/jophiel/userAccount';
import { createUserAvatarAPI } from './api/jophiel/userAvatar';
import { createUserInfoAPI } from './api/jophiel/userInfo';
import { createUserRegistrationWebAPI } from './api/jophiel/userRegistration';
import { createUserWebAPI } from './api/jophiel/userWeb';
import { createContestAPI } from './api/uriel/contest';
import { createContestWebAPI } from './api/uriel/contestWeb';
import { createContestAnnouncementAPI } from './api/uriel/contestAnnouncement';
import { createContestClarificationAPI } from './api/uriel/contestClarification';
import { createContestContestantAPI } from './api/uriel/contestContestant';
import { createContestProblemAPI } from './api/uriel/contestProblem';
import { createContestScoreboardAPI } from './api/uriel/contestScoreboard';
import { createContestSubmissionAPI } from './api/uriel/contestSubmission';
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
        sessionAPI: createSessionAPI(),
        legacySessionAPI: createLegacySessionAPI(),
        myAPI: createMyAPI(),
        profileAPI: createProfileAPI(),
        userAPI: createUserAPI(),
        userAccountAPI: createUserAccountAPI(),
        userAvatarAPI: createUserAvatarAPI(),
        userInfoAPI: createUserInfoAPI(),
        userRegistrationWebAPI: createUserRegistrationWebAPI(),
        userWebAPI: createUserWebAPI(),
        contestAPI: createContestAPI(),
        contestWebAPI: createContestWebAPI(),
        contestAnnouncementAPI: createContestAnnouncementAPI(),
        contestClarificationAPI: createContestClarificationAPI(),
        contestContestantAPI: createContestContestantAPI(),
        contestProblemAPI: createContestProblemAPI(),
        contestScoreboardAPI: createContestScoreboardAPI(),
        contestSubmissionAPI: createContestSubmissionAPI(),
        toastActions,
      }),
      routerMiddleware(history)
    )
  )
);

export const persistor = persistStore(store);
