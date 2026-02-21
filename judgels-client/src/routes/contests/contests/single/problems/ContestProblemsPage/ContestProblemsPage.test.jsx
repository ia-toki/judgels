import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { setSession } from '../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestProblemsPage from './ContestProblemsPage';

import * as contestProblemActions from '../modules/contestProblemActions';

vi.mock('../modules/contestProblemActions');

describe('ContestProblemsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  let problems;
  let canManage;

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    contestProblemActions.getProblems.mockReturnValue(
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

    await act(async () =>
      render(
        <WebPrefsProvider initialPrefs={{ statementLanguage: 'en' }}>
          <QueryClientProviderWrapper>
            <TestRouter initialEntries={['/contests/contest-slug/problems']} path="/contests/$contestSlug/problems">
              <ContestProblemsPage />
            </TestRouter>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      )
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
        await waitFor(() => {
          const section = document.querySelector('div.content-card__section');
          expect(section).not.toBeNull();
          const buttons = Array.from(section.querySelectorAll('button'));
          expect(buttons.map(b => b.textContent)).toEqual(['Edit problems']);
        });
      });
    });
  });

  describe('content', () => {
    describe('when there are no problems', () => {
      beforeEach(async () => {
        problems = [];
        await renderComponent();
      });

      it('shows placeholder text and no problems', async () => {
        expect(await screen.findByText(/no problems/i)).toBeInTheDocument();
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

      it('shows the problems', async () => {
        await waitFor(() => {
          const cards = document.querySelectorAll('div.contest-problem-card');
          expect([...cards].map(card => card.textContent)).toEqual([
            'B. Problem B [100 points]8 submissions left',
            'A. Problem ACLOSED',
          ]);
        });
      });
    });
  });
});
