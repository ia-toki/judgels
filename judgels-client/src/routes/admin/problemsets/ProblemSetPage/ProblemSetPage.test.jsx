import { act, render, screen, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJerahmeel } from '../../../../utils/nock';
import ProblemSetPage from './ProblemSetPage';

describe('ProblemSetPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockJerahmeel().get('/problemsets/slug/problemset-1').reply(200, {
      jid: 'JIDPROBLEMSET1',
      slug: 'problemset-1',
      name: 'Problemset 1',
      archiveJid: 'JIDARCHIVE1',
      description: 'Description 1',
      contestTime: 1609459200000,
    });

    nockJerahmeel()
      .get('/archives')
      .reply(200, {
        data: [{ jid: 'JIDARCHIVE1', slug: 'archive-1', name: 'Archive 1' }],
      });

    nockJerahmeel()
      .get('/problemsets/JIDPROBLEMSET1/problems')
      .reply(200, {
        data: [{ alias: 'A', problemJid: 'JIDPROBLEM1', type: 'PROGRAMMING', contestJids: [] }],
        problemsMap: { JIDPROBLEM1: { slug: 'problem-1' } },
        contestsMap: {},
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/problemsets/problemset-1']} path="/admin/problemsets/$problemSetSlug">
            <ProblemSetPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders problemset details', async () => {
    await renderComponent();

    await screen.findByText(/Problemset 1/);

    const tables = screen.getAllByRole('table');

    expect(
      within(tables[0])
        .getAllByRole('row')
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([
      ['Slug', 'problemset-1'],
      ['Name', 'Problemset 1'],
      ['Archive slug', 'archive-1'],
      ['Contest time', '2021-01-01T00:00:00.000Z'],
      ['Description', 'Description 1'],
    ]);

    expect(
      within(tables[1])
        .getAllByRole('row')
        .slice(1)
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([['A', 'problem-1', '']]);
  });
});
