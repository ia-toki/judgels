import { mount } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest, userJid, user } from '../../../../../../fixtures/state';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsReducer';

import ContestClarificationsPage from './ContestClarificationsPage';
import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestClarificationActions from '../modules/contestClarificationActions';

jest.mock('../modules/contestClarificationActions');

describe('ContestClarificationsPage', () => {
  let wrapper;

  const response = {
    data: { page: [], totalCount: 0 },
    config: {
      canCreate: true,
      canSupervise: false,
      canManage: false,
      problemJids: ['problemJid1', 'problemJid2'],
    },
    profilesMap: { [userJid]: { username: 'username' } },
    problemAliasesMap: { problemJid1: 'A', problemJid2: 'B' },
    problemNamesMap: { problemJid1: 'Problem 1', problemJid2: 'Problem 2' },
  };

  const render = () => {
    const store = createStore(
      combineReducers({
        session: sessionReducer,
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
        form: formReducer,
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser(user));
    store.dispatch(PutContest(contest));
    store.dispatch(PutStatementLanguage('en'));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestClarificationsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    contestClarificationActions.getClarifications.mockReturnValue(() => Promise.resolve(response));
    contestClarificationActions.createClarification.mockReturnValue(() => Promise.resolve({}));
    contestClarificationActions.answerClarification.mockReturnValue(() => Promise.resolve({}));
  });

  describe('when there are no clarifications', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no clarifications', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No clarifications.');
      expect(wrapper.find(ContestClarificationCard)).toHaveLength(0);
    });
  });

  describe('when there are clarifications', () => {
    beforeEach(() => {
      const clarifications = [
        {
          jid: 'jid1',
          userJid,
          time: 12345,
        },
        {
          jid: 'jid2',
          userJid,
          time: 12345,
        },
      ];
      contestClarificationActions.getClarifications.mockReturnValue(() =>
        Promise.resolve({ ...response, data: { page: clarifications, totalCount: 2 } })
      );

      render();
    });

    it('shows the clarifications', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find(ContestClarificationCard)).toHaveLength(2);
    });
  });
});
