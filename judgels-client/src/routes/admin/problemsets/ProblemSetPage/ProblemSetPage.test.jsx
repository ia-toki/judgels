import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemSetPage from './ProblemSetPage';

describe('ProblemSetPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockApi().get('/problemsets/slug/problemset-1').reply(200, {
      jid: 'JIDPROBLEMSET1',
      slug: 'problemset-1',
      name: 'Problemset 1',
      archiveJid: 'JIDARCHIVE1',
      description: 'Description 1',
      contestTime: 1609459200000,
    });

    nockApi()
      .get('/archives')
      .reply(200, {
        data: [{ jid: 'JIDARCHIVE1', slug: 'archive-1', name: 'Archive 1' }],
      });

    nockApi()
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

  test('details', async () => {
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

  test('general form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[0]);

    const slug = screen.getByRole('textbox', { name: /^slug/i });
    expect(slug).toHaveValue('problemset-1');
    await user.clear(slug);
    await user.type(slug, 'new-problemset');

    const name = screen.getByRole('textbox', { name: /^name/i });
    expect(name).toHaveValue('Problemset 1');
    await user.clear(name);
    await user.type(name, 'New Problemset');

    const archive = screen.getByRole('textbox', { name: /archive slug/i });
    expect(archive).toHaveValue('archive-1');
    await user.clear(archive);
    await user.type(archive, 'new-archive');

    nockApi()
      .post('/problemsets/JIDPROBLEMSET1', body => {
        return body.slug === 'new-problemset' && body.name === 'New Problemset' && body.archiveSlug === 'new-archive';
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('problems form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const editButtons = await screen.findAllByRole('button', { name: /edit/i });
    await user.click(editButtons[1]);

    const problems = document.querySelector('textarea[name="problems"]');
    expect(problems).toHaveValue('A,problem-1');
    await user.clear(problems);
    await user.type(problems, 'A,new-problem');

    nockApi()
      .put('/problemsets/JIDPROBLEMSET1/problems', [
        { alias: 'A', slug: 'new-problem', type: 'PROGRAMMING', contestSlugs: [] },
      ])
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
