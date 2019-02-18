import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest, contestJid } from 'fixtures/state';
import { Submission } from 'modules/api/sandalphon/submissionProgramming';
import { ContestSubmissionsResponse } from 'modules/api/uriel/contestSubmissionProgramming';

import { createContestSubmissionsPage } from './ContestSubmissionsPage';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestSubmissionsPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let contestProgrammingSubmissionActions: jest.Mocked<any>;

  const response: ContestSubmissionsResponse = {
    data: { page: [], totalCount: 0 },
    config: {
      canSupervise: true,
      canManage: true,
      userJids: [],
      problemJids: [],
    },
    profilesMap: {
      userJid1: { username: 'user1' },
      userJid2: { username: 'user2' },
    },
    problemAliasesMap: {
      problemJid1: 'A',
      problemJid2: 'B',
    },
  };

  const render = () => {
    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    const ContestSubmissionsPage = createContestSubmissionsPage(contestProgrammingSubmissionActions);

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestSubmissionsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    contestProgrammingSubmissionActions = {
      getSubmissions: jest.fn().mockReturnValue(() => Promise.resolve(response)),
    };
  });

  describe('when there are no submissions', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no submissions', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No submissions.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are submissions', () => {
    beforeEach(() => {
      const submissions: Submission[] = [
        {
          jid: 'jid1',
          userJid: 'userJid1',
          problemJid: 'problemJid1',
          containerJid: contestJid,
          gradingEngine: 'Batch',
          gradingLanguage: 'Cpp',
          time: 123,
        } as Submission,
        {
          jid: 'jid2',
          userJid: 'userJid2',
          problemJid: 'problemJid2',
          containerJid: contestJid,
          gradingEngine: 'Batch',
          gradingLanguage: 'Cpp',
          time: 456,
        } as Submission,
      ];
      contestProgrammingSubmissionActions.getSubmissions.mockReturnValue(() =>
        Promise.resolve({ ...response, data: { page: submissions, totalCount: 2 } })
      );

      render();
    });

    it('shows the submissions', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find('tr')).toHaveLength(3);
    });
  });
});
