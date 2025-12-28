import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsReducer';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestProblemsPage from './ContestProblemsPage';

import * as contestProblemActions from '../modules/contestProblemActions';

vi.mock('../modules/contestProblemActions');

describe('ContestProblemsPage', () => {
  let problems;
  let canManage;

  const renderComponent = async () => {
    contestProblemActions.getProblems.mockReturnValue(() =>
      Promise.resolve({
        data: problems,
        problemsMap: {
          problemJid1: {
            slug: 'problem-a',
            type: ProblemType.PROGRAMMING,
            defaultLanguage: 'id',
            titlesByLanguage: {
              id: 'Soal A',
              en: 'Problem A',
            },
          },
          problemJid2: {
            slug: 'problem-b',
            type: ProblemType.PROGRAMMING,
            defaultLanguage: 'id',
            titlesByLanguage: {
              id: 'Soal B',
              en: 'Problem B',
            },
          },
        },
        totalSubmissionsMap: { problemJid1: 0, problemJid2: 2 },
        config: {
          canManage,
        },
      })
    );

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid' }));
    store.dispatch(PutContest({ jid: 'contestJid' }));
    store.dispatch(PutStatementLanguage('en'));

    render(
      <Provider store={store}>
        <MemoryRouter>
          <ContestProblemsPage />
        </MemoryRouter>
      </Provider>
    );

    await waitFor(() => expect(contestProblemActions.getProblems).toHaveBeenCalled());
  };

  describe('action buttons', () => {
    beforeEach(() => {
      problems = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await renderComponent();
      });

      it('shows no buttons', async () => {
        const section = document.querySelector('div.content-card__section');
        const buttons = section ? section.querySelectorAll('button') : [];
        expect(buttons).toHaveLength(0);
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await renderComponent();
      });

      it('shows action buttons', async () => {
        const section = document.querySelector('div.content-card__section');
        const buttons = Array.from(section.querySelectorAll('button'));
        expect(buttons.map(b => b.textContent)).toEqual(['Edit problems']);
      });
    });
  });

  describe('content', () => {
    describe('when there are no problems', () => {
      beforeEach(async () => {
        problems = [];
        await renderComponent();
      });

      it('shows placeholder text and no problems', () => {
        expect(screen.getByText(/no problems/i)).toBeInTheDocument();
        const cards = document.querySelectorAll('div.contest-problem-card');
        expect(cards).toHaveLength(0);
      });
    });

    describe('when there are problems', () => {
      beforeEach(async () => {
        problems = [
          {
            problemJid: 'problemJid1',
            alias: 'A',
            status: 'CLOSED',
            submissionsLimit: null,
          },
          {
            problemJid: 'problemJid2',
            alias: 'B',
            status: 'OPEN',
            submissionsLimit: 10,
            points: 100,
          },
        ];
        await renderComponent();
      });

      it('shows the problems', () => {
        const cards = document.querySelectorAll('div.contest-problem-card');
        expect([...cards].map(card => card.textContent)).toEqual([
          'B. Problem B [100 points]8 submissions left',
          'A. Problem ACLOSED',
        ]);
      });
    });
  });
});
