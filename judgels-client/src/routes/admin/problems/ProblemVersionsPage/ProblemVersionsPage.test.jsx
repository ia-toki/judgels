import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemVersionsPage from './ProblemVersionsPage';

describe('ProblemVersionsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ hasLocalChanges = true } = {}) => {
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, {
        data: { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type: 'PROGRAMMING', authorJid: 'JIDUSER1' },
        setterJidsMap: {},
        topicTags: [],
        hasLocalChanges,
        config: { canEdit: true, canManage: true },
        profilesMap: {},
      });
    nockApi()
      .get('/v4/problems/JIDPROG1/versions')
      .reply(200, {
        data: [
          {
            hash: 'bbbbbbb222222',
            userJid: 'JIDUSER2',
            time: 1700000000000,
            title: 'Update statement',
            description: 'Reworded the statement.',
          },
          { hash: 'aaaaaaa111111', userJid: 'JIDUSER1', time: 1600000000000, title: 'Initial commit', description: '' },
        ],
        profilesMap: {
          JIDUSER1: { username: 'andi' },
          JIDUSER2: { username: 'budi' },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/JIDPROG1/versions']}
            path="/admin/problems/$problemJid/versions"
          >
            <ProblemVersionsPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByRole('heading', { name: 'History' });
  };

  test('history', async () => {
    await renderComponent({ hasLocalChanges: false });

    const rows = within(screen.getByRole('table')).getAllByRole('row').slice(1);
    expect(
      rows.map(row => [
        ...[...row.querySelectorAll('td')].slice(0, 3).map(cell => cell.textContent),
        within(row)
          .queryAllByRole('button')
          .map(button => button.textContent),
      ])
    ).toEqual([
      ['bbbbbbb', 'Update statementReworded the statement.', 'budi', []],
      ['aaaaaaa', 'Initial commit', 'andi', ['Restore']],
    ]);
    expect(screen.getByText(/no local changes/i)).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Commit' })).not.toBeInTheDocument();
  });

  test('does not offer to restore while there are local changes', async () => {
    await renderComponent();
    expect(screen.queryByRole('button', { name: /restore/i })).not.toBeInTheDocument();
  });

  test('commits the local changes', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.type(screen.getByRole('textbox', { name: /title/i }), 'Fix typo');
    await user.type(document.querySelector('textarea[name="description"]'), 'In the samples.');

    nockApi()
      .post('/v4/problems/JIDPROG1/versions/local/commit', { title: 'Fix typo', description: 'In the samples.' })
      .reply(200);

    await user.click(screen.getByRole('button', { name: 'Commit' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('asks to rebase when the local changes are outdated', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.type(screen.getByRole('textbox', { name: /title/i }), 'Fix typo');

    nockApi()
      .post('/v4/problems/JIDPROG1/versions/local/commit', { title: 'Fix typo', description: '' })
      .reply(400, { message: 'ProblemVersionLocalChangesOutdated' });

    await user.click(screen.getByRole('button', { name: 'Commit' }));

    expect(await screen.findByText(/rebase your local changes first/i)).toBeInTheDocument();
  });

  test('rebases the local changes', async () => {
    await renderComponent();
    const user = userEvent.setup();

    nockApi().post('/v4/problems/JIDPROG1/versions/local/rebase').reply(200);

    await user.click(screen.getByRole('button', { name: /rebase/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('discards the local changes', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: 'Discard' }));
    expect(await screen.findByText('Discard your local changes?')).toBeInTheDocument();

    nockApi().delete('/v4/problems/JIDPROG1/versions/local').reply(200);

    await user.click(within(screen.getByRole('alertdialog')).getByRole('button', { name: 'Discard' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('restores a version', async () => {
    await renderComponent({ hasLocalChanges: false });
    const user = userEvent.setup();

    await user.click(screen.getByRole('button', { name: 'Restore aaaaaaa' }));
    expect(await screen.findByText(/restore version aaaaaaa\?/i)).toBeInTheDocument();

    nockApi().post('/v4/problems/JIDPROG1/versions/aaaaaaa111111/restore').reply(200);

    await user.click(screen.getByRole('button', { name: 'Restore' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
