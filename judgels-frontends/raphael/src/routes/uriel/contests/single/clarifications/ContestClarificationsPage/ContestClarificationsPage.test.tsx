import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest, userJid } from 'fixtures/state';
import { ContestClarification, ContestClarificationsResponse } from 'modules/api/uriel/contestClarification';
import { PutUser, sessionReducer } from 'modules/session/sessionReducer';
import { PutStatementLanguage, webPrefsReducer } from 'modules/webPrefs/webPrefsReducer';

import { createContestClarificationsPage } from './ContestClarificationsPage';
import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestClarificationsPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let contestClarificationActions: jest.Mocked<any>;

  const response: ContestClarificationsResponse = {
    data: { page: [], totalCount: 0 },
    config: {
      canCreate: true,
      canSupervise: false,
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
        uriel: combineReducers({ contest: contestReducer }),
        webPrefs: webPrefsReducer,
        form: formReducer,
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser.create({ jid: userJid, username: 'username' }));
    store.dispatch(PutContest.create(contest));
    store.dispatch(PutStatementLanguage.create('en'));

    const ContestClarificationsPage = createContestClarificationsPage(contestClarificationActions);

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
    contestClarificationActions = {
      getClarifications: jest.fn().mockReturnValue(() => Promise.resolve(response)),
      createClarification: jest.fn().mockReturnValue(() => Promise.resolve({})),
      answerClarification: jest.fn().mockReturnValue(() => Promise.resolve({})),
    };
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
      const clarifications: ContestClarification[] = [
        {
          jid: 'jid1',
          userJid,
          time: 12345,
        } as ContestClarification,
        {
          jid: 'jid2',
          userJid,
          time: 12345,
        } as ContestClarification,
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
