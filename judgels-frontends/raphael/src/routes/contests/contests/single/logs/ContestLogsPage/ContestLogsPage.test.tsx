import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest, contestJid } from '../../../../../../fixtures/state';
import { ContestLog, ContestLogsResponse } from '../../../../../../modules/api/uriel/contestLog';
import ContestLogsPage from './ContestLogsPage';
import { contestReducer, PutContest } from '../../../modules/contestReducer';
import * as contestLogActions from '../modules/contestLogActions';

jest.mock('../modules/contestLogActions');

describe('ContestLogsPage', () => {
  let wrapper: ReactWrapper<any, any>;

  const response: ContestLogsResponse = {
    data: { page: [], totalCount: 0 },
    config: {
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
    const store: any = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestLogsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    (contestLogActions.getLogs as jest.Mock).mockReturnValue(() => Promise.resolve(response));
  });

  describe('when there are no logs', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no logs', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No logs.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are logs', () => {
    beforeEach(() => {
      const logs: ContestLog[] = [
        {
          contestJid: contestJid,
          userJid: 'userJid1',
          event: 'OPEN_PROBLEM',
          problemJid: 'problemJid1',
          time: 123,
        } as ContestLog,
        {
          contestJid: contestJid,
          userJid: 'userJid2',
          event: 'CREATE_CLARIFICATION',
          problemJid: 'problemJid1',
          time: 456,
        } as ContestLog,
      ];
      (contestLogActions.getLogs as jest.Mock).mockReturnValue(() =>
        Promise.resolve({ ...response, data: { page: logs, totalCount: 2 } })
      );

      render();
    });

    it('shows the logs', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find('tr')).toHaveLength(3);
    });
  });
});
