import { act, render, screen, waitFor } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestProblemsPage from './ContestProblemsPage';

describe('ContestProblemsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ problems = [], canManage } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/problems')
      .reply(200, {
        data: problems,
        problemsMap: {
          problemJid1: {
            slug: 'problem-a',
            type: 'PROGRAMMING',
            defaultLanguage: 'id',
            titlesByLanguage: {
              id: 'Soal A',
              en: 'Problem A',
            },
          },
          problemJid2: {
            slug: 'problem-b',
            type: 'PROGRAMMING',
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
      });

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
  };

  test('renders no action buttons when not canManage', async () => {
    await renderComponent({ canManage: false });
    await screen.findByRole('heading', { name: 'Problems' });
    expect(screen.queryByRole('button', { name: /edit problems/i })).not.toBeInTheDocument();
  });

  test('renders action buttons when canManage', async () => {
    await renderComponent({ canManage: true });
    expect(await screen.findByRole('button', { name: /edit problems/i })).toBeInTheDocument();
  });

  test('renders placeholder when there are no problems', async () => {
    await renderComponent({ problems: [] });

    expect(await screen.findByText(/no problems/i)).toBeInTheDocument();
    expect(screen.queryAllByRole('link')).toHaveLength(0);
  });

  test('renders the problems when there are problems', async () => {
    await renderComponent({
      problems: [
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
      ],
    });

    await waitFor(() => {
      const cards = screen.getAllByRole('link');
      expect(cards.map(card => card.textContent)).toEqual([
        'B. Problem B [100 points]8 submissions left',
        'A. Problem ACLOSED',
      ]);
    });
  });
});
