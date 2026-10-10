import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemEditorialPage from './ProblemEditorialPage';

describe('ProblemEditorialPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ canEdit = true } = {}) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, {
        data: { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type: 'PROGRAMMING', authorJid: 'JIDUSER1' },
        setterJidsMap: { WRITER: ['JIDUSER2'] },
        topicTags: [],
        hasLocalChanges: false,
        hasEditorial: true,
        config: { canEdit, canManage: canEdit },
        profilesMap: { JIDUSER2: { username: 'budi' } },
      });
    nockApi()
      .get('/v4/problems/JIDPROG1/editorial/languages')
      .reply(200, { enabledLanguages: ['id-ID', 'en-US'], disabledLanguages: ['fr-FR'], defaultLanguage: 'id-ID' });
    nockApi()
      .get('/v4/problems/JIDPROG1/editorial')
      .query({ language: 'id-ID' })
      .reply(200, { text: '<p>Urutkan apel. <img src="render/apel.png"></p>' });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/editorial/content']}
            path="/admin/problems/$problemSlug/editorial/content"
          >
            <ProblemEditorialPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText(/urutkan apel/i);
  };

  test('previews the editorial in the default language', async () => {
    await renderComponent();

    expect(screen.getByRole('combobox', { name: /language/i })).toHaveValue('id-ID');
    expect(screen.getAllByRole('option').map(option => option.textContent)).toEqual([
      'English (en-US)',
      'Indonesian (id-ID)',
    ]);
    expect(screen.getByText(/written by/i)).toHaveTextContent('budi');
    expect(document.querySelector('img').getAttribute('src')).toMatch(
      /\/v2\/problems\/JIDPROG1\/editorials\/render\/apel\.png$/
    );
  });

  test('switches the language', async () => {
    await renderComponent();

    nockApi()
      .get('/v4/problems/JIDPROG1/editorial')
      .query({ language: 'en-US' })
      .reply(200, { text: '<p>Sort the apples.</p>' });

    const user = userEvent.setup();
    await user.selectOptions(screen.getByRole('combobox', { name: /language/i }), 'en-US');

    expect(await screen.findByText(/sort the apples/i)).toBeInTheDocument();
  });

  test('hides the edit button when the problem cannot be edited', async () => {
    await renderComponent({ canEdit: false });
    expect(screen.queryByRole('button', { name: /edit/i })).not.toBeInTheDocument();
  });

  test('edit form', async () => {
    await renderComponent();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /edit/i }));

    const text = screen.getByRole('textbox', { name: /text/i });
    await user.clear(text);
    await user.type(text, 'Urutkan jeruk.');

    nockApi()
      .put('/v4/problems/JIDPROG1/editorial', { text: 'Urutkan jeruk.' })
      .query({ language: 'id-ID' })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
