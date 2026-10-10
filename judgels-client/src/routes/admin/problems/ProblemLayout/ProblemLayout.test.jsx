import { act, render, screen } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemLayout from './ProblemLayout';

describe('ProblemLayout', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ hasLocalChanges, type = 'PROGRAMMING' }) => {
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, {
        data: { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type, authorJid: 'JIDUSER1' },
        setterJidsMap: {},
        topicTags: [],
        hasLocalChanges,
        config: { canEdit: true, canManage: true },
        profilesMap: {},
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/problems/JIDPROG1/general']} path="/admin/problems/$problemJid/general">
            <ProblemLayout />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText(/problem-1/);
  };

  test('renders the tabs', async () => {
    await renderComponent({ hasLocalChanges: false });

    expect(
      ['General', 'Statements', 'Languages', 'Media', 'Grading'].map(name =>
        screen.getByRole('link', { name }).getAttribute('href')
      )
    ).toEqual([
      '/admin/problems/JIDPROG1/general',
      '/admin/problems/JIDPROG1/statements',
      '/admin/problems/JIDPROG1/languages',
      '/admin/problems/JIDPROG1/media',
      '/admin/problems/JIDPROG1/grading',
    ]);
    expect(screen.queryByRole('link', { name: 'Items' })).not.toBeInTheDocument();
    expect(screen.queryByText(/uncommitted changes/i)).not.toBeInTheDocument();
  });

  test('shows the items tab of a bundle problem instead of the grading tab', async () => {
    await renderComponent({ hasLocalChanges: false, type: 'BUNDLE' });

    expect(screen.getByRole('link', { name: 'Items' })).toHaveAttribute('href', '/admin/problems/JIDPROG1/items');
    expect(screen.queryByRole('link', { name: 'Grading' })).not.toBeInTheDocument();
  });

  test('warns about uncommitted changes', async () => {
    await renderComponent({ hasLocalChanges: true });
    expect(screen.getByText(/uncommitted changes/i)).toBeInTheDocument();
  });
});
