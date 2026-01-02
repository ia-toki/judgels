import { act, render, screen, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { TestRouter } from '../../../../../../test/RouterWrapper';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestSupervisorsPage from './ContestSupervisorsPage';

import * as contestSupervisorActions from '../../modules/contestSupervisorActions';

vi.mock('../../modules/contestSupervisorActions');

describe('ContestSupervisorsPage', () => {
  let supervisors;

  const renderComponent = async () => {
    contestSupervisorActions.getSupervisors.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: supervisors,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
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
            <ContestSupervisorsPage />
          </TestRouter>
        </Provider>
      )
    );
  };

  describe('action buttons', () => {
    beforeEach(async () => {
      supervisors = [];
      await renderComponent();
    });

    it('shows action buttons', () => {
      expect(screen.getByRole('button', { name: /add\/update supervisors/i })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /remove supervisors/i })).toBeInTheDocument();
    });
  });

  describe('content', () => {
    describe('when there are no supervisors', () => {
      beforeEach(async () => {
        supervisors = [];
        await renderComponent();
      });

      it('shows placeholder text and no supervisors', () => {
        expect(screen.getByText(/no supervisors/i)).toBeInTheDocument();
        const rows = screen.queryAllByRole('row');
        expect(rows).toHaveLength(0);
      });
    });

    describe('when there are supervisors', () => {
      beforeEach(async () => {
        supervisors = [
          {
            userJid: 'userJid1',
            managementPermissions: ['ANNOUNCEMENT', 'PROBLEM'],
          },
          {
            userJid: 'userJid2',
            managementPermissions: ['ALL'],
          },
        ];
        await renderComponent();
      });

      it('shows the supervisors', () => {
        const rows = screen.getAllByRole('row').slice(1);
        expect(
          rows.map(row =>
            within(row)
              .queryAllByRole('cell')
              .map(cell => cell.textContent)
          )
        ).toEqual([
          ['username1', 'ANNCPROB'],
          ['username2', 'ALL'],
        ]);
      });
    });
  });
});
