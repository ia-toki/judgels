import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import LessonStatementPage from './LessonStatementPage';

describe('LessonStatementPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ canEdit = true } = {}) => {
    nockApi().get('/v4/lessons/slug/lesson-1').reply(200, { jid: 'JIDLESS1', slug: 'lesson-1' });
    nockApi()
      .get('/v4/lessons/JIDLESS1')
      .reply(200, {
        data: { id: 1, jid: 'JIDLESS1', slug: 'lesson-1', authorJid: 'JIDUSER1' },
        hasLocalChanges: false,
        config: { canEdit },
        profilesMap: {},
      });
    nockApi()
      .get('/v4/lessons/JIDLESS1/statement/languages')
      .reply(200, { enabledLanguages: ['id-ID', 'en-US'], disabledLanguages: ['fr-FR'], defaultLanguage: 'id-ID' });
    nockApi()
      .get('/v4/lessons/JIDLESS1/statement')
      .query({ language: 'id-ID' })
      .reply(200, { title: 'Pengantar', text: '<p>Bacalah ini. <img src="render/gambar.png"></p>' });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/lessons/lesson-1/statements']}
            path="/admin/lessons/$lessonSlug/statements"
          >
            <LessonStatementPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText('Pengantar');
  };

  test('previews the statement in the default language', async () => {
    await renderComponent();

    expect(screen.getByRole('combobox', { name: /language/i })).toHaveValue('id-ID');
    expect(screen.getAllByRole('option').map(option => option.textContent)).toEqual([
      'English (en-US)',
      'Indonesian (id-ID)',
    ]);
    expect(screen.getByRole('heading', { name: 'Pengantar' })).toBeInTheDocument();
    expect(screen.getByText(/bacalah ini/i)).toBeInTheDocument();
    expect(document.querySelector('img').getAttribute('src')).toMatch(/\/v2\/lessons\/JIDLESS1\/render\/gambar\.png$/);
  });

  test('switches the language', async () => {
    await renderComponent();

    nockApi()
      .get('/v4/lessons/JIDLESS1/statement')
      .query({ language: 'en-US' })
      .reply(200, { title: 'Introduction', text: '<p>Read this.</p>' });

    const user = userEvent.setup();
    await user.selectOptions(screen.getByRole('combobox', { name: /language/i }), 'en-US');

    expect(await screen.findByText('Introduction')).toBeInTheDocument();
  });

  test('hides the edit button when the lesson cannot be edited', async () => {
    await renderComponent({ canEdit: false });
    expect(screen.queryByRole('button', { name: /edit/i })).not.toBeInTheDocument();
  });

  test('edit form', async () => {
    await renderComponent();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /edit/i }));

    const title = screen.getByRole('textbox', { name: /title/i });
    expect(title).toHaveValue('Pengantar');
    await user.clear(title);
    await user.type(title, 'Pendahuluan');

    const text = screen.getByRole('textbox', { name: /text/i });
    await user.clear(text);
    await user.type(text, 'Bacalah itu.');

    nockApi()
      .post('/v4/lessons/JIDLESS1/statement', { title: 'Pendahuluan', text: 'Bacalah itu.' })
      .query({ language: 'id-ID' })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
