import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemGradingEnginePage from './ProblemGradingEnginePage';

describe('ProblemGradingEnginePage', () => {
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
            initialEntries={['/admin/problems/JIDPROG1/grading/engine']}
            path="/admin/problems/$problemJid/grading/engine"
          >
            <ProblemGradingEnginePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  const renderComponent = async ({ canEdit = true } = {}) => {
    nockProblem(canEdit);
    nockApi()
      .get('/v4/problems/JIDPROG1/grading/config')
      .reply(200, { engine: 'Batch', config: { timeLimit: 2000, memoryLimit: 65536, testData: [] } });

    await renderPage();
    await screen.findByRole('heading', { name: 'Engine' });
  };

  test('shows the current engine', async () => {
    await renderComponent();

    expect(screen.getByRole('combobox', { name: 'Grading engine' })).toHaveValue('Batch');
    expect(screen.getByRole('button', { name: 'Save' })).toBeDisabled();
  });

  test('is read-only for a viewer', async () => {
    await renderComponent({ canEdit: false });

    expect(screen.getByRole('combobox', { name: 'Grading engine' })).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Save' })).not.toBeInTheDocument();
  });

  test('changes the engine after confirming', async () => {
    await renderComponent();
    const user = userEvent.setup();

    await user.selectOptions(screen.getByRole('combobox', { name: 'Grading engine' }), 'Interactive with Subtasks');
    await user.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByText(/this resets the grading config/i)).toBeInTheDocument();

    nockApi().put('/v4/problems/JIDPROG1/grading/engine', { engine: 'InteractiveWithSubtasks' }).reply(200);

    await user.click(screen.getByRole('button', { name: 'Change' }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
