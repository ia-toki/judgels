import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import LessonGeneralPage from './LessonGeneralPage';

describe('LessonGeneralPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ canEdit = true } = {}) => {
    nockApi()
      .get('/v4/lessons/JIDLESS1')
      .reply(200, {
        data: { id: 1, jid: 'JIDLESS1', slug: 'lesson-1', authorJid: 'JIDUSER1', additionalNote: 'A note' },
        hasLocalChanges: false,
        config: { canEdit },
        profilesMap: { JIDUSER1: { username: 'author' } },
      });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/lessons/JIDLESS1/general']} path="/admin/lessons/$lessonJid/general">
            <LessonGeneralPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText('lesson-1');
  };

  test('details', async () => {
    await renderComponent();

    const rows = within(screen.getByRole('table')).getAllByRole('row');
    expect(rows.map(row => [...row.querySelectorAll('td')].map(cell => cell.textContent))).toEqual([
      ['JID', 'JIDLESS1'],
      ['Slug', 'lesson-1'],
      ['Author', 'author'],
      ['Additional note', 'A note'],
    ]);
  });

  test('hides the edit button when the lesson cannot be edited', async () => {
    await renderComponent({ canEdit: false });
    expect(screen.queryByRole('button', { name: /edit/i })).not.toBeInTheDocument();
  });

  test('edit form', async () => {
    await renderComponent();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /edit/i }));

    const slug = await screen.findByRole('textbox', { name: /slug/i });
    expect(slug).toHaveValue('lesson-1');
    await user.clear(slug);
    await user.type(slug, 'lesson-one');

    nockApi()
      .post('/v4/lessons/JIDLESS1', { slug: 'lesson-one', additionalNote: 'A note' })
      .reply(200, { jid: 'JIDLESS1', slug: 'lesson-one' });

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('shows an error when the slug already exists', async () => {
    await renderComponent();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /edit/i }));

    const slug = await screen.findByRole('textbox', { name: /slug/i });
    await user.clear(slug);
    await user.type(slug, 'old-lesson');

    nockApi().post('/v4/lessons/JIDLESS1').reply(400, { message: 'LessonSlugAlreadyExists' });

    await user.click(screen.getByRole('button', { name: /save/i }));

    expect(await screen.findByText(/slug already exists/i)).toBeInTheDocument();
  });
});
