import { act, render, screen, waitFor } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import UserViewPage from './UserViewPage';

describe('UserViewPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockJophiel().get('/users/JIDUSER123').reply(200, {
      jid: 'JIDUSER123',
      username: 'andi',
      email: 'andi@example.com',
    });

    nockJophiel().options('/users/JIDUSER123/info').reply(200);
    nockJophiel().get('/users/JIDUSER123/info').reply(200, {
      name: 'Andi Smith',
      gender: 'MALE',
      country: 'ID',
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/users/JIDUSER123']} path="/admin/users/$userJid">
            <UserViewPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders user details', async () => {
    await renderComponent();

    expect(await screen.findByText('andi')).toBeInTheDocument();
    expect(screen.getByText('JIDUSER123')).toBeInTheDocument();
    expect(screen.getByText('andi@example.com')).toBeInTheDocument();
  });

  test('renders user info', async () => {
    await renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Andi Smith')).toBeInTheDocument();
    });
    expect(screen.getByText('MALE')).toBeInTheDocument();
    expect(screen.getByText('ID')).toBeInTheDocument();
  });
});
