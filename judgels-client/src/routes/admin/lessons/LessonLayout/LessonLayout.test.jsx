import { act, render, screen } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import LessonLayout from './LessonLayout';

describe('LessonLayout', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ hasLocalChanges }) => {
    nockApi()
      .get('/v4/lessons/JIDLESS1')
      .reply(200, {
        data: { id: 1, jid: 'JIDLESS1', slug: 'lesson-1', authorJid: 'JIDUSER1' },
        hasLocalChanges,
        config: { canEdit: true },
        profilesMap: {},
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/lessons/JIDLESS1/general']} path="/admin/lessons/$lessonJid/general">
            <LessonLayout />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText(/lesson-1/);
  };

  test('renders the tabs', async () => {
    await renderComponent({ hasLocalChanges: false });

    expect(
      ['General', 'Statements', 'Languages', 'Media'].map(name =>
        screen.getByRole('link', { name }).getAttribute('href')
      )
    ).toEqual([
      '/admin/lessons/JIDLESS1/general',
      '/admin/lessons/JIDLESS1/statements',
      '/admin/lessons/JIDLESS1/languages',
      '/admin/lessons/JIDLESS1/media',
    ]);
    expect(screen.queryByText(/uncommitted changes/i)).not.toBeInTheDocument();
  });

  test('warns about uncommitted changes', async () => {
    await renderComponent({ hasLocalChanges: true });
    expect(screen.getByText(/uncommitted changes/i)).toBeInTheDocument();
  });
});
