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

  const renderComponent = async ({ hasEditorial }) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi().get('/v4/problems/JIDPROG1/editorial/exists').reply(200, hasEditorial);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/editorial/content']}
            path="/admin/problems/$problemSlug/editorial/content"
          >
            <ProblemEditorialLayout />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders the tabs of an editorial', async () => {
    await renderComponent({ hasEditorial: true });

    await screen.findByRole('link', { name: 'Editorial' });

    expect(
      ['Editorial', 'Languages', 'Media'].map(name => screen.getByRole('link', { name }).getAttribute('href'))
    ).toEqual([
      '/admin/problems/problem-1/editorial/content',
      '/admin/problems/problem-1/editorial/languages',
      '/admin/problems/problem-1/editorial/media',
    ]);
    expect(screen.queryByRole('button', { name: /create editorial/i })).not.toBeInTheDocument();
  });

  test('creates the editorial of a problem that has none', async () => {
    await renderComponent({ hasEditorial: false });

    expect(await screen.findByRole('button', { name: /create editorial/i })).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Languages' })).not.toBeInTheDocument();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: 'English (en-US)' }));
    await user.type(screen.getByRole('combobox', { name: 'Filter languages' }), 'indo');
    await user.click(screen.getByText('Indonesian (id-ID)'));

    nockApi().post('/v4/problems/JIDPROG1/editorial', { initialLanguage: 'id-ID' }).reply(200);

    await user.click(screen.getByRole('button', { name: /create editorial/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
