import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestSupervisorsPage from './ContestSupervisorsPage';

describe('ContestSupervisorsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    supervisors = [
      {
        userJid: 'userJid1',
        managementPermissions: ['ANNOUNCEMENT', 'PROBLEM'],
      },
      {
        userJid: 'userJid2',
        managementPermissions: ['ALL'],
      },
    ],
  } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/supervisors')
      .reply(200, {
        data: {
          page: supervisors,
          totalCount: supervisors.length,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug/supervisors']} path="/contests/$contestSlug/supervisors">
            <ContestSupervisorsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders action buttons', async () => {
    await renderComponent();
    expect(await screen.findByRole('button', { name: /add\/update supervisors/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /remove supervisors/i })).toBeInTheDocument();
  });

  test('renders placeholder when there are no supervisors', async () => {
    await renderComponent({ supervisors: [] });
    expect(await screen.findByText(/no supervisors/i)).toBeInTheDocument();
    const rows = screen.queryAllByRole('row');
    expect(rows).toHaveLength(0);
  });

  test('renders supervisors when there are supervisors', async () => {
    await renderComponent();
    await waitFor(() => {
      expect(screen.getAllByRole('row').length).toBeGreaterThan(1);
    });
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
