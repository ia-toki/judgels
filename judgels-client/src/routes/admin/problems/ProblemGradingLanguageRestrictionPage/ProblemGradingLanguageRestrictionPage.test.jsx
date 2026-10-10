import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemGradingLanguageRestrictionPage from './ProblemGradingLanguageRestrictionPage';

describe('ProblemGradingLanguageRestrictionPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const nockProblem = canEdit =>
    nockApi()
      .get('/v4/problems/JIDPROG1')
      .reply(200, {
        data: { id: 1, jid: 'JIDPROG1', slug: 'problem-1', type: 'PROGRAMMING', authorJid: 'JIDUSER1' },
        setterJidsMap: {},
        topicTags: [],
        hasLocalChanges: false,
        config: { canEdit, canManage: canEdit },
        profilesMap: {},
      });

  const renderPage = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/admin/problems/JIDPROG1/grading/languages']}
            path="/admin/problems/$problemJid/grading/languages"
          >
            <ProblemGradingLanguageRestrictionPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  const renderComponent = async ({ canEdit = true, allowedLanguageNames = [] } = {}) => {
    nockProblem(canEdit);
    nockApi().get('/v4/problems/JIDPROG1/grading/language-restriction').reply(200, { allowedLanguageNames });

    await renderPage();
    await screen.findByRole('heading', { name: 'Languages' });
  };

  const getCheckedLanguages = () =>
    screen
      .getAllByRole('checkbox')
      .filter(checkbox => checkbox.checked)
      .map(checkbox => checkbox.closest('label').textContent);

  test('checks every language when all are allowed', async () => {
    await renderComponent();

    expect(screen.getByRole('checkbox', { name: 'Allow all' })).toBeChecked();
    expect(screen.getByRole('checkbox', { name: 'C++17' })).toBeChecked();
    expect(screen.getByRole('checkbox', { name: 'C++17' })).toBeDisabled();
  });

  test('checks only the allowed languages', async () => {
    await renderComponent({ allowedLanguageNames: ['Cpp17', 'Python3'] });

    expect(getCheckedLanguages()).toEqual(['C++17', 'Python 3']);
  });

  test('is read-only for a viewer', async () => {
    await renderComponent({ canEdit: false, allowedLanguageNames: ['Cpp17'] });

    screen.getAllByRole('checkbox').forEach(checkbox => expect(checkbox).toBeDisabled());
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
  });

  test('restricts the languages', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.click(screen.getByRole('checkbox', { name: 'Allow all' }));
    expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();

    await user.click(screen.getByRole('checkbox', { name: 'Python 3' }));
    await user.click(screen.getByRole('checkbox', { name: 'C++17' }));

    nockApi()
      .put('/v4/problems/JIDPROG1/grading/language-restriction', { allowedLanguageNames: ['Cpp17', 'Python3'] })
      .reply(200);

    await user.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('allows all languages again', async () => {
    await renderComponent({ allowedLanguageNames: ['Cpp17'] });
    const user = userEvent.setup();

    await user.click(screen.getByRole('checkbox', { name: 'Allow all' }));

    nockApi().put('/v4/problems/JIDPROG1/grading/language-restriction', { allowedLanguageNames: [] }).reply(200);

    await user.click(screen.getByRole('button', { name: 'Save' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
