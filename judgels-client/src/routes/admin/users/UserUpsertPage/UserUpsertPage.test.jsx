import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockApi } from '../../../../utils/nock';
import UserUpsertPage from './UserUpsertPage';

describe('UserUpsertPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/users/-/upsert']}>
            <UserUpsertPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('form', async () => {
    await renderComponent();
    const user = userEvent.setup();

    const textarea = screen.getByRole('textbox');
    await user.type(textarea, 'username,password,email\nandi,pass1,andi@example.com');

    nockApi()
      .post('/v2/users/batch-upsert', 'username,password,email\nandi,pass1,andi@example.com')
      .reply(200, {
        createdUsernames: ['andi'],
        updatedUsernames: [],
      });

    await user.click(screen.getByRole('button', { name: /submit/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));

    await waitFor(() => {
      expect(screen.getByText('1 users created, 0 users updated.')).toBeInTheDocument();
    });
    expect(screen.getByText('andi')).toBeInTheDocument();
  });
});
