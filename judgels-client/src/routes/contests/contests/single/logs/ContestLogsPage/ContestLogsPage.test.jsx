import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestLogsPage from './ContestLogsPage';

import * as contestLogActions from '../modules/contestLogActions';

jest.mock('../modules/contestLogActions');

describe('ContestLogsPage', () => {
  let logs;

  const renderComponent = async () => {
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

    await act(async () =>
      render(
        <Provider store={store}>
          <MemoryRouter>
            <ContestLogsPage />
          </MemoryRouter>
        </Provider>
      )
    );
  };

  describe('when there are no logs', () => {
    beforeEach(async () => {
      logs = [];
      await renderComponent();
    });

    it('shows placeholder text and no logs', () => {
      expect(screen.getByText(/no logs/i)).toBeInTheDocument();
      expect(screen.queryByRole('row')).not.toBeInTheDocument();
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
      await renderComponent();
    });

    it('shows the logs', () => {
      const rows = screen.getAllByRole('row').slice(1);
      expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
        ['username1', 'OPEN_PROBLEM', 'A', '1 day ago '],
        ['username2', 'OPEN_CLARIFICATIONS', '', '1 day ago '],
      ]);
    });
  });
});
