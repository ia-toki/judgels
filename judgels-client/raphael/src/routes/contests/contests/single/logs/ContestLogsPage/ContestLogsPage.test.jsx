import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestLogsPage from './ContestLogsPage';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestLogActions from '../modules/contestLogActions';

jest.mock('../modules/contestLogActions');

describe('ContestLogsPage', () => {
  let wrapper;
  let logs;

  const render = async () => {
    contestLogActions.getLogs.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: logs,
        },
        config: {
          userJids: [],
          problemJids: [],
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        problemAliasesMap: {
          problemJid1: 'A',
          problemJid2: 'B',
        },
      })
    );

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestLogsPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no logs', () => {
    beforeEach(async () => {
      logs = [];
      await render();
    });

    it('shows placeholder text and no logs', () => {
      expect(wrapper.text()).toContain('No logs.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are logs', () => {
    beforeEach(async () => {
      logs = [
        {
          contestJid: 'contestJid',
          userJid: 'userJid1',
          event: 'OPEN_PROBLEM',
          problemJid: 'problemJid1',
          time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
        },
        {
          contestJid: 'contestJid',
          userJid: 'userJid2',
          event: 'OPEN_CLARIFICATIONS',
          time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
        },
      ];
      await render();
    });

    it('shows the logs', () => {
      expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text()))).toEqual([
        [],
        ['username1', 'OPEN_PROBLEM', 'A', '1 day ago '],
        ['username2', 'OPEN_CLARIFICATIONS', '', '1 day ago '],
      ]);
    });
  });
});
