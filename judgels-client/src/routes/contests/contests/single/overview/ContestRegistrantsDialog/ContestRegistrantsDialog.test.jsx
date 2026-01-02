import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestRegistrantsDialog from './ContestRegistrantsDialog';

import * as contestContestantActions from '../../modules/contestContestantActions';

vi.mock('../../modules/contestContestantActions');

describe('ContestRegistrantsDialog', () => {
  beforeEach(async () => {
    contestContestantActions.getApprovedContestants.mockReturnValue(() =>
      Promise.resolve({
        data: ['userJid1', 'userJid2', 'userJid3', 'userJid4', 'userJid5', 'userJid6'],
        profilesMap: {
          userJid1: { country: 'TH', username: 'username1', rating: { publicRating: 2000 } },
          userJid2: { country: 'ID', username: 'username2', rating: { publicRating: 1000 } },
          userJid3: { country: 'ID', username: 'username3', rating: { publicRating: 3000 } },
          userJid4: { country: 'ID', username: 'username4', rating: { publicRating: 2000 } },
          userJid5: { country: 'ID', username: 'username5', rating: { publicRating: 1000 } },
          userJid6: { username: 'username6' },
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
          <TestRouter>
            <ContestRegistrantsDialog />
          </TestRouter>
        </Provider>
      )
    );
  });

  test('table', () => {
    const rows = screen.getAllByRole('row').slice(1);
    expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
      ['Indonesia', 'username3'],
      ['Indonesia', 'username4'],
      ['Thailand', 'username1'],
      ['Indonesia', 'username2'],
      ['Indonesia', 'username5'],
      ['', 'username6'],
    ]);
  });
});
