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

  const renderComponent = async ({ hasLocalChanges, canEdit = true }) => {
    nockApi().get('/v4/lessons/slug/lesson-1').reply(200, { jid: 'JIDLESS1', slug: 'lesson-1' });
    nockApi()
      .get('/v4/lessons/JIDLESS1')
      .reply(200, {
        data: { id: 1, jid: 'JIDLESS1', slug: 'lesson-1', authorJid: 'JIDUSER1' },
        hasLocalChanges,
        config: { canEdit },
        profilesMap: {},
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/lessons/lesson-1/general']} path="/admin/lessons/$lessonSlug/general">
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
      ['General', 'Statements', 'Languages', 'Media', 'Versions'].map(name =>
        screen.getByRole('link', { name }).getAttribute('href')
      )
    ).toEqual([
      '/admin/lessons/lesson-1/general',
      '/admin/lessons/lesson-1/statements',
      '/admin/lessons/lesson-1/languages',
      '/admin/lessons/lesson-1/media',
      '/admin/lessons/lesson-1/versions',
    ]);
    expect(screen.queryByText(/uncommitted changes/i)).not.toBeInTheDocument();
  });

  test('hides the versions tab when the lesson cannot be edited', async () => {
    await renderComponent({ hasLocalChanges: false, canEdit: false });
    expect(screen.queryByRole('link', { name: 'Versions' })).not.toBeInTheDocument();
  });

  test('warns about uncommitted changes', async () => {
    await renderComponent({ hasLocalChanges: true });
    expect(screen.getByText(/uncommitted changes/i)).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /commit your changes/i })).toHaveAttribute(
      'href',
      '/admin/lessons/lesson-1/versions'
    );
  });
});
