import { act, render, screen, within } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import contestReducer, { PutContest } from '../../../../modules/contestReducer';
import ContestSubmissionsPage from './ContestSubmissionsPage';

import * as contestSubmissionActions from '../modules/contestSubmissionActions';

vi.mock('../modules/contestSubmissionActions');

describe('ContestSubmissionsPage', () => {
  let submissions;
  let canSupervise;
  let canManage;

  const renderComponent = async () => {
    contestSubmissionActions.getSubmissions.mockReturnValue(() =>
      Promise.resolve({
        data: { page: submissions },
        config: {
          canSupervise,
          canManage,
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
            <ContestSubmissionsPage />
          </MemoryRouter>
        </Provider>
      )
    );
  };

  describe('when there are no submissions', () => {
    beforeEach(async () => {
      submissions = [];
      await renderComponent();
    });

    it('shows placeholder text and no submissions', () => {
      expect(screen.getByText(/no submissions/i)).toBeInTheDocument();
      const rows = screen.queryAllByRole('row');
      expect(rows).toHaveLength(0);
    });
  });

  describe('when there are submissions', () => {
    beforeEach(() => {
      submissions = [
        {
          id: 20,
          jid: 'jid1',
          userJid: 'userJid1',
          problemJid: 'problemJid1',
          containerJid: 'contestJid',
          gradingEngine: 'Batch',
          gradingLanguage: 'Cpp17',
          time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
        },
        {
          id: 10,
          jid: 'jid2',
          userJid: 'userJid2',
          problemJid: 'problemJid2',
          containerJid: 'contestJid',
          gradingEngine: 'Batch',
          gradingLanguage: 'Cpp17',
          time: new Date(new Date().setDate(new Date().getDate() - 2)).getTime(),
          latestGrading: {
            verdict: { code: 'WA' },
            score: 70,
          },
        },
      ];
    });

    describe('when not canSupervise', () => {
      beforeEach(async () => {
        canSupervise = false;
        canManage = false;
        await renderComponent();
      });

      it('shows the submissions', () => {
        const rows = screen.getAllByRole('row').slice(1);
        const data = rows.map(row => {
          const cells = within(row).queryAllByRole('cell');
          return cells.map(cell => cell.textContent.trim());
        });

        expect(data).toEqual([
          ['20', 'A', 'C++17', '', '1 day ago', 'search'],
          ['10', 'B', 'C++17', 'Wrong Answer70', '2 days ago', 'search'],
        ]);
      });
    });

    describe('when canSupervise', () => {
      beforeEach(async () => {
        canSupervise = true;
        canManage = false;
        await renderComponent();
      });

      it('shows the submissions', () => {
        const rows = screen.getAllByRole('row').slice(1);
        const data = rows.map(row => {
          const cells = within(row).queryAllByRole('cell');
          return cells.map(cell => cell.textContent.trim());
        });

        expect(data).toEqual([
          ['20', 'user1', 'A', 'C++17', '', '1 day ago', 'search'],
          ['10', 'user2', 'B', 'C++17', 'Wrong Answer70', '2 days ago', 'search'],
        ]);
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canSupervise = true;
        canManage = true;
        await renderComponent();
      });

      it('shows the submissions', () => {
        const rows = screen.getAllByRole('row').slice(1);
        const data = rows.map(row => {
          const cells = within(row).queryAllByRole('cell');
          return cells.map(cell => cell.textContent.replace(/\s+/g, ' ').trim());
        });

        expect(data).toEqual([
          ['20 refresh', 'user1', 'A', 'C++17', '', '1 day ago', 'search'],
          ['10 refresh', 'user2', 'B', 'C++17', 'Wrong Answer70', '2 days ago', 'search'],
        ]);
      });
    });
  });
});
