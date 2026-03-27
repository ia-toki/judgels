import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

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

    expect(await screen.findAllByText(/andi/));
    expect(screen.getByText('JIDUSER123')).toBeInTheDocument();
    expect(screen.getByText('andi@example.com')).toBeInTheDocument();
  });

  test('renders user info', async () => {
    await renderComponent();

    expect(await screen.findByText(/Andi Smith/)).toBeInTheDocument();
    expect(screen.getByText('Andi Smith')).toBeInTheDocument();
    expect(screen.getByText('MALE')).toBeInTheDocument();
    expect(screen.getByText('ID')).toBeInTheDocument();
  });

  test('user info form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const name = document.querySelector('input[name="name"]');
    expect(name).toHaveValue('Andi Smith');
    await user.clear(name);
    await user.type(name, 'Caca');

    const gender = document.querySelector('select[name="gender"]');
    expect(gender).toHaveValue('MALE');
    await user.selectOptions(gender, 'FEMALE');

    const country = document.querySelector('select[name="country"]');
    expect(country).toHaveValue('ID');
    await user.selectOptions(country, 'US');

    nockJophiel()
      .put('/users/JIDUSER123/info', {
        name: 'Caca',
        gender: 'FEMALE',
        country: 'US',
      })
      .reply(200, {
        name: 'Caca',
        gender: 'FEMALE',
        country: 'US',
      });

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
