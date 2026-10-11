import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemStatementPage from './ProblemStatementPage';

describe('ProblemStatementPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({ canEdit = true, type = 'PROGRAMMING', reasonNotAllowedToSubmit } = {}) => {
    nockApi().get('/v4/problems/slug/problem-1').reply(200, { jid: 'JIDPROG1', slug: 'problem-1' });
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type, authorJid: 'JIDUSER1' });
    nockApi().get('/v4/problems/JIDPROG1/permissions').reply(200, { canEdit, reasonNotAllowedToSubmit });
    if (canEdit && type === 'PROGRAMMING') {
      nockApi()
        .get('/v4/problems/JIDPROG1/submissions/programming/config')
        .reply(200, {
          sourceKeys: { source: 'Source code' },
          gradingEngine: 'Batch',
          gradingLanguageRestriction: { allowedLanguageNames: ['Cpp11'] },
        });
    }
    nockApi()
      .get('/v4/problems/JIDPROG1/statement/languages')
      .reply(200, { enabledLanguages: ['id-ID', 'en-US'], disabledLanguages: ['fr-FR'], defaultLanguage: 'id-ID' });
    nockApi()
      .get('/v4/problems/JIDPROG1/statement')
      .query({ language: 'id-ID' })
      .reply(200, { title: 'Kotak Apel', text: '<p>Hitung apel. <img src="render/apel.png"></p>' });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/problem-1/statements']}
            path="/admin/problems/$problemSlug/statements"
          >
            <ProblemStatementPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    await screen.findByText('Kotak Apel');
  };

  test('previews the statement in the default language', async () => {
    await renderComponent();

    expect(screen.getByRole('combobox', { name: /language/i })).toHaveValue('id-ID');
    expect(screen.getAllByRole('option').map(option => option.textContent)).toEqual([
      'English (en-US)',
      'Indonesian (id-ID)',
    ]);
    expect(screen.getByText(/hitung apel/i)).toBeInTheDocument();
    expect(document.querySelector('img').getAttribute('src')).toMatch(/\/v2\/problems\/JIDPROG1\/render\/apel\.png$/);
  });

  const nockBundleItems = () => {
    nockApi()
      .get('/v4/problems/JIDPROG1/items')
      .query({ language: 'id-ID' })
      .reply(200, {
        data: [
          { jid: 'JIDITEM1', type: 'STATEMENT', meta: '', config: { statement: '<p>Bacalah.</p>' } },
          {
            jid: 'JIDITEM2',
            type: 'MULTIPLE_CHOICE',
            number: 1,
            meta: '',
            config: {
              statement: '<p>Berapa apel?</p>',
              score: 4,
              penalty: -1,
              choices: [
                { alias: 'a', content: 'satu <img src="render/satu.png">', isCorrect: true },
                { alias: 'b', content: 'dua', isCorrect: false },
              ],
            },
          },
        ],
      });
  };

  test('previews a bundle problem with its items', async () => {
    nockBundleItems();
    await renderComponent({ type: 'BUNDLE', canEdit: false });

    expect(screen.getByText(/hitung apel/i)).toBeInTheDocument();
    expect(screen.getByText('Bacalah.')).toBeInTheDocument();
    expect(screen.getByText('Berapa apel?')).toBeInTheDocument();
    expect(screen.getByText('dua')).toBeInTheDocument();
    expect([...document.querySelectorAll('img')].map(img => img.getAttribute('src'))).toEqual([
      expect.stringMatching(/\/v2\/problems\/JIDPROG1\/render\/apel\.png$/),
      expect.stringMatching(/\/v2\/problems\/JIDPROG1\/render\/satu\.png$/),
    ]);
    screen.getAllByRole('radio').forEach(radio => expect(radio).toBeDisabled());
    expect(screen.queryByRole('button', { name: 'Submit' })).not.toBeInTheDocument();
  });

  test('submits a solution', async () => {
    await renderComponent();
    const user = userEvent.setup();

    const file = new File(['int main() {}'], 'solution.cpp', { type: 'text/plain' });
    Object.defineProperty(file, 'size', { value: 1000 });
    await user.upload(document.querySelector('input[name="sourceFiles.source"]'), file);

    nockApi()
      .post(
        '/v4/problems/JIDPROG1/submissions/programming',
        body =>
          body.includes('name="gradingLanguage"\r\n\r\nCpp11\r\n') &&
          body.includes('name="sourceFiles.source"') &&
          body.includes('int main() {}')
      )
      .reply(200, { id: 10, jid: 'JIDSUBM1' });

    await user.click(screen.getByRole('button', { name: 'Submit' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('submits the answers of a bundle problem together', async () => {
    nockBundleItems();
    await renderComponent({ type: 'BUNDLE' });
    const user = userEvent.setup();

    await user.click(screen.getByRole('radio', { name: /dua/ }));

    nockApi()
      .post('/v4/problems/JIDPROG1/submissions/bundle', { answers: { JIDITEM2: 'b' }, languageCode: 'id-ID' })
      .reply(200);

    await user.click(screen.getByRole('button', { name: 'Submit' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('tells why a solution cannot be submitted', async () => {
    await renderComponent({ reasonNotAllowedToSubmit: 'Submission not allowed if there are local changes.' });

    expect(screen.getByText('Submission not allowed if there are local changes.')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Submit' })).not.toBeInTheDocument();
  });

  test('hides the submission form when the problem cannot be edited', async () => {
    await renderComponent({ canEdit: false, reasonNotAllowedToSubmit: 'Submission not allowed.' });

    expect(screen.queryByText('Submission not allowed.')).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Submit' })).not.toBeInTheDocument();
  });

  test('switches the language', async () => {
    await renderComponent();

    nockApi()
      .get('/v4/problems/JIDPROG1/statement')
      .query({ language: 'en-US' })
      .reply(200, { title: 'Boxes of Apples', text: '<p>Count the apples.</p>' });

    const user = userEvent.setup();
    await user.selectOptions(screen.getByRole('combobox', { name: /language/i }), 'en-US');

    expect(await screen.findByText('Boxes of Apples')).toBeInTheDocument();
  });

  test('hides the edit button when the problem cannot be edited', async () => {
    await renderComponent({ canEdit: false });
    expect(screen.queryByRole('button', { name: /edit/i })).not.toBeInTheDocument();
  });

  test('edit form', async () => {
    await renderComponent();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /edit/i }));

    const title = screen.getByRole('textbox', { name: /title/i });
    expect(title).toHaveValue('Kotak Apel');
    await user.clear(title);
    await user.type(title, 'Kotak Jeruk');

    const text = screen.getByRole('textbox', { name: /text/i });
    await user.clear(text);
    await user.type(text, 'Hitung jeruk.');

    nockApi()
      .post('/v4/problems/JIDPROG1/statement', { title: 'Kotak Jeruk', text: 'Hitung jeruk.' })
      .query({ language: 'id-ID' })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('shows the manuals', async () => {
    await renderComponent();

    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: /edit/i }));
    await user.click(screen.getByRole('button', { name: /formatting manual/i }));

    expect(await screen.findByText(/inserting spoiler/i)).toBeInTheDocument();
  });
});
