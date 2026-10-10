import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemEditorialLayout from './ProblemEditorialLayout';

describe('ProblemEditorialLayout', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ hasEditorial, canEdit = true }) => {
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, {
        data: { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type: 'PROGRAMMING', authorJid: 'JIDUSER1' },
        setterJidsMap: {},
        topicTags: [],
        hasLocalChanges: false,
        hasEditorial,
        config: { canEdit, canManage: canEdit },
        profilesMap: {},
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/JIDPROG1/editorial/content']}
            path="/admin/problems/$problemJid/editorial/content"
          >
            <ProblemEditorialLayout />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders the tabs of an editorial', async () => {
    await renderComponent({ hasEditorial: true });

    await screen.findByRole('link', { name: 'Content' });

    expect(
      ['Content', 'Languages', 'Media'].map(name => screen.getByRole('link', { name }).getAttribute('href'))
    ).toEqual([
      '/admin/problems/JIDPROG1/editorial/content',
      '/admin/problems/JIDPROG1/editorial/languages',
      '/admin/problems/JIDPROG1/editorial/media',
    ]);
    expect(screen.queryByRole('button', { name: /create editorial/i })).not.toBeInTheDocument();
  });

  test('creates the editorial of a problem that has none', async () => {
    await renderComponent({ hasEditorial: false });

    expect(await screen.findByText(/no editorial/i)).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Languages' })).not.toBeInTheDocument();

    const user = userEvent.setup();
    const select = screen.getByRole('combobox', { name: /initial language/i });
    expect(select).toHaveValue('en-US');
    await user.selectOptions(select, 'id-ID');

    nockApi().post('/v4/problems/JIDPROG1/editorial', { initialLanguage: 'id-ID' }).reply(200);

    await user.click(screen.getByRole('button', { name: /create editorial/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('only tells a viewer that there is no editorial', async () => {
    await renderComponent({ hasEditorial: false, canEdit: false });

    expect(await screen.findByText(/no editorial/i)).toBeInTheDocument();
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
    expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
  });
});
