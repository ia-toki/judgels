import { act, render, screen, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestContestantsPage from './ContestContestantsPage';

import * as contestContestantActions from '../../modules/contestContestantActions';

vi.mock('../../modules/contestContestantActions');

describe('ContestContestantsPage', () => {
  let contestants;
  let canManage;

  const renderComponent = async () => {
    contestContestantActions.getContestants.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: contestants,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
        config: {
          canManage,
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
            <ContestContestantsPage />
          </MemoryRouter>
        </Provider>
      )
    );
  };

  describe('action buttons', () => {
    beforeEach(() => {
      contestants = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await renderComponent();
      });

      it('shows no buttons', () => {
        expect(screen.queryByRole('button', { name: /add contestants/i })).not.toBeInTheDocument();
        expect(screen.queryByRole('button', { name: /remove contestants/i })).not.toBeInTheDocument();
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', () => {
        expect(screen.getByRole('button', { name: /add contestants/i })).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /remove contestants/i })).toBeInTheDocument();
      });
    });
  });

  describe('content', () => {
    describe('when there are no contestants', () => {
      beforeEach(async () => {
        contestants = [];
        await renderComponent();
      });

      it('shows placeholder text and no contestants', () => {
        expect(screen.getByText(/no contestants/i)).toBeInTheDocument();
        expect(screen.queryByRole('row')).not.toBeInTheDocument();
      });
    });

    describe('when there are contestants', () => {
      beforeEach(async () => {
        contestants = [
          {
            userJid: 'userJid1',
          },
          {
            userJid: 'userJid2',
          },
        ];
        await renderComponent();
      });

      it('shows the contestants', () => {
        const rows = screen.getAllByRole('row');
        expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
          [],
          ['1', 'username1'],
          ['2', 'username2'],
        ]);
      });
    });
  });
});
