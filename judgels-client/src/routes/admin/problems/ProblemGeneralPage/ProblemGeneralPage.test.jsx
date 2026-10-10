import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemGeneralPage from './ProblemGeneralPage';

describe('ProblemGeneralPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ canEdit = true } = {}) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, {
        data: {
          id: 1,
          jid: 'JIDPROG1',
          slug: 'problem-1',
          type: 'PROGRAMMING',
          authorJid: 'JIDUSER1',
          additionalNote: 'A note',
        },
        setterJidsMap: { WRITER: ['JIDUSER2'], TESTER: ['JIDUSER3', 'JIDUSER2'] },
        topicTags: ['topic-graph: shortest path', 'topic-graph'],
        hasLocalChanges: false,
        config: { canEdit, canManage: canEdit },
        profilesMap: {
          JIDUSER1: { username: 'author' },
          JIDUSER2: { username: 'writer' },
          JIDUSER3: { username: 'tester' },
        },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/general']}
            path="/admin/problems/$problemSlug/general"
          >
            <ProblemGeneralPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText('problem-1');
  };

  const nockTags = () => {
    nockApi()
      .get('/v4/problems/tags')
      .reply(200, {
        data: [],
        topicTags: ['topic-ad hoc', 'topic-graph', 'topic-graph: bipartite', 'topic-graph: shortest path'],
      });
  };

  test('details', async () => {
    await renderComponent();

    const rows = within(screen.getByRole('table')).getAllByRole('row');
    expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
      ['JID', 'JIDPROG1'],
      ['Slug', 'problem-1'],
      ['Type', 'Programming'],
      ['Author', 'author'],
      ['Additional note', 'A note'],
      ['Writers', 'writer'],
      ['Developers', ''],
      ['Testers', 'tester, writer'],
      ['Editorialists', ''],
      ['Tags', 'graph, graph: shortest path'],
    ]);
  });

  test('hides the edit button when the problem cannot be edited', async () => {
    await renderComponent({ canEdit: false });
    expect(screen.queryByRole('button', { name: /edit/i })).not.toBeInTheDocument();
  });

  test('edit form', async () => {
    await renderComponent();
    nockTags();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /edit/i }));

    const slug = await screen.findByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('problem-1');
    await user.clear(slug);
    await user.type(slug, 'problem-one');

    const developers = screen.getByRole('textbox', { name: /developers/i });
    await user.type(developers, 'dev1, dev2');

    await user.click(screen.getByRole('checkbox', { name: 'ad hoc' }));

    nockApi()
      .post('/v4/problems/JIDPROG1', {
        slug: 'problem-one',
        additionalNote: 'A note',
        setterUsernamesMap: {
          WRITER: ['writer'],
          DEVELOPER: ['dev1', 'dev2'],
          TESTER: ['tester', 'writer'],
          EDITORIALIST: [],
        },
        topicTags: ['topic-graph: shortest path', 'topic-graph', 'topic-ad hoc'],
      })
      .reply(200, { jid: 'JIDPROG1', slug: 'problem-one' });

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('shows which setters are not found', async () => {
    await renderComponent();
    nockTags();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /edit/i }));

    await user.type(await screen.findByRole('textbox', { name: /editorialists/i }), 'ghost');

    nockApi()
      .post('/v4/problems/JIDPROG1')
      .reply(400, { message: 'ProblemSetterUsernamesNotFound', args: { usernames: 'ghost' } });

    await user.click(screen.getByRole('button', { name: /save/i }));

    expect(await screen.findByText('Users not found: ghost')).toBeInTheDocument();
  });
});
