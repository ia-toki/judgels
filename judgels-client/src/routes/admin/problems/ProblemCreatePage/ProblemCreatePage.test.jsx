import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import ProblemCreatePage from './ProblemCreatePage';

describe('ProblemCreatePage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/problems/-/new']}>
            <ProblemCreatePage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );

    return userEvent.setup();
  };

  test('creates a problem', async () => {
    const user = await renderComponent();

    await user.type(screen.getByRole('textbox', { name: /slug/i }), 'new-problem');
    await user.type(screen.getByRole('textbox', { name: /additional note/i }), 'A note');

    nockApi()
      .post('/v4/problems', {
        slug: 'new-problem',
        gradingEngine: 'Batch',
        additionalNote: 'A note',
        initialLanguage: 'en-US',
      })
      .reply(200, { jid: 'JIDPROG1', slug: 'new-problem' });

    await user.click(screen.getByRole('button', { name: /create/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('shows an error when the slug already exists', async () => {
    const user = await renderComponent();

    await user.type(screen.getByRole('textbox', { name: /slug/i }), 'old-problem');

    nockApi()
      .post('/v4/problems', {
        slug: 'old-problem',
        gradingEngine: 'Batch',
        additionalNote: '',
        initialLanguage: 'en-US',
      })
      .reply(400, { message: 'ProblemSlugAlreadyExists' });

    await user.click(screen.getByRole('button', { name: /create/i }));

    expect(await screen.findByText(/slug already exists/i)).toBeInTheDocument();
  });
});
