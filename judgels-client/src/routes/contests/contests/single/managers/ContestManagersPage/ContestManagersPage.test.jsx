import { act, render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestManagersPage from './ContestManagersPage';

import * as contestManagerActions from '../modules/contestManagerActions';

vi.mock('../modules/contestManagerActions');

describe('ContestManagersPage', () => {
  let managers;
  let canManage;

  const renderComponent = async () => {
    contestManagerActions.getManagers.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: managers,
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
            <ContestManagersPage />
          </MemoryRouter>
        </Provider>
      )
    );
  };

  describe('action buttons', () => {
    beforeEach(() => {
      managers = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await renderComponent();
      });

      it('shows no buttons', () => {
        expect(screen.queryByRole('button', { name: /add managers/i })).not.toBeInTheDocument();
        expect(screen.queryByRole('button', { name: /remove managers/i })).not.toBeInTheDocument();
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', () => {
        expect(screen.getByRole('button', { name: /add managers/i })).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /remove managers/i })).toBeInTheDocument();
      });
    });
  });

  describe('content', () => {
    describe('when there are no managers', () => {
      beforeEach(async () => {
        managers = [];
        await renderComponent();
      });

      it('shows placeholder text and no managers', () => {
        expect(screen.getByText(/no managers/i)).toBeInTheDocument();
        expect(screen.queryByRole('row')).not.toBeInTheDocument();
      });
    });

    describe('when there are managers', () => {
      beforeEach(async () => {
        managers = [
          {
            userJid: 'userJid1',
          },
          {
            userJid: 'userJid2',
          },
        ];
        await renderComponent();
      });

      it('shows the managers', () => {
        const rows = screen.getAllByRole('row').slice(1);
        expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
          ['username1'],
          ['username2'],
        ]);
      });
    });
  });
});
