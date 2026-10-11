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
    nockApi().get('/v4/problems/JIDPROG1').reply(200, {
      id: 1,
      jid: 'JIDPROG1',
      slug: 'problem-1',
      type: 'PROGRAMMING',
      authorJid: 'JIDUSER1',
      additionalNote: 'A note',
    });
    nockApi().get('/v4/problems/JIDPROG1/permissions').reply(200, { canEdit });
    nockApi().get('/v2/profiles/JIDUSER1/basic').reply(200, { username: 'author' });
    nockApi()
      .get('/v4/problems/JIDPROG1/setters')
      .reply(200, {
        data: { WRITER: ['JIDUSER2'], TESTER: ['JIDUSER3', 'JIDUSER2'] },
        profilesMap: { JIDUSER2: { username: 'writer' }, JIDUSER3: { username: 'tester' } },
      });
    nockApi().get('/v4/problems/JIDPROG1/topic-tags').reply(200, ['topic-graph: shortest path', 'topic-graph']);

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

  const getSection = name => screen.getByRole('region', { name });

  const getRows = section =>
    within(section)
      .getAllByRole('row')
      .map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent));

  test('details', async () => {
    await renderComponent();

    expect(getRows(getSection('General'))).toEqual([
      ['JID', 'JIDPROG1'],
      ['Slug', 'problem-1'],
      ['Type', 'Programming'],
      ['Author', 'author'],
      ['Additional note', 'A note'],
    ]);
    expect(getRows(getSection('Setters'))).toEqual([
      ['Writers', 'writer'],
      ['Developers', ''],
      ['Testers', 'tester, writer'],
      ['Editorialists', ''],
    ]);
    expect(getRows(getSection('Tags'))).toEqual([['Tags', 'graph, graph: shortest path']]);
  });

  test('hides the edit buttons when the problem cannot be edited', async () => {
    await renderComponent({ canEdit: false });
    expect(screen.queryByRole('button', { name: /edit/i })).not.toBeInTheDocument();
  });

  test('general edit form', async () => {
    await renderComponent();

    const user = userEvent.setup();
    const section = getSection('General');
    await user.click(within(section).getByRole('button', { name: /edit/i }));

    const slug = await within(section).findByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('problem-1');
    await user.clear(slug);
    await user.type(slug, 'problem-one');

    nockApi()
      .post('/v4/problems/JIDPROG1', { slug: 'problem-one', additionalNote: 'A note' })
      .reply(200, { jid: 'JIDPROG1', slug: 'problem-one' });

    await user.click(within(section).getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('setters edit form', async () => {
    await renderComponent();

    const user = userEvent.setup();
    const section = getSection('Setters');
    await user.click(within(section).getByRole('button', { name: /edit/i }));

    const developers = await within(section).findByRole('textbox', { name: /developers/i });
    await user.type(developers, 'dev1, dev2');

    nockApi()
      .put('/v4/problems/JIDPROG1/setters', {
        WRITER: ['writer'],
        DEVELOPER: ['dev1', 'dev2'],
        TESTER: ['tester', 'writer'],
        EDITORIALIST: [],
      })
      .reply(200);

    await user.click(within(section).getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('shows which setters are not found', async () => {
    await renderComponent();

    const user = userEvent.setup();
    const section = getSection('Setters');
    await user.click(within(section).getByRole('button', { name: /edit/i }));

    await user.type(await within(section).findByRole('textbox', { name: /editorialists/i }), 'ghost');

    nockApi()
      .put('/v4/problems/JIDPROG1/setters')
      .reply(400, { message: 'ProblemSetterUsernamesNotFound', args: { usernames: 'ghost' } });

    await user.click(within(section).getByRole('button', { name: /save/i }));

    expect(await screen.findByText('Users not found: ghost')).toBeInTheDocument();
  });

  test('tags edit form', async () => {
    await renderComponent();
    nockTags();

    const user = userEvent.setup();
    const section = getSection('Tags');
    await user.click(within(section).getByRole('button', { name: /edit/i }));

    await user.click(await within(section).findByRole('checkbox', { name: 'ad hoc' }));

    nockApi()
      .put('/v4/problems/JIDPROG1/topic-tags', ['topic-graph: shortest path', 'topic-graph', 'topic-ad hoc'])
      .reply(200);

    await user.click(within(section).getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
