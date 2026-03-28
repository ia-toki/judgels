import { act, render, screen, within } from '@testing-library/react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../test/RouterWrapper';
import { nockJophiel } from '../../../../utils/nock';
import UserPage from './UserPage';

describe('UserPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async () => {
    nockJophiel().get('/users/username/andi').reply(200, {
      jid: 'JIDUSER123',
      username: 'andi',
      email: 'andi@example.com',
    });

    nockJophiel().get('/users/JIDUSER123/info').reply(200, {
      name: 'Andi Smith',
      gender: 'MALE',
      country: 'ID',
      homeAddress: '123 Main St',
      shirtSize: 'M',
      institutionName: 'MIT',
      institutionCountry: 'US',
      institutionProvince: 'Massachusetts',
      institutionCity: 'Cambridge',
    });

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/admin/users/andi']} path="/admin/users/$username">
            <UserPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders user details', async () => {
    await renderComponent();

    await screen.findAllByText(/andi/);
    const tables = screen.getAllByRole('table');
    expect(
      within(tables[0])
        .getAllByRole('row')
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([
      ['JID', 'JIDUSER123'],
      ['Email', 'andi@example.com'],
    ]);

    expect(
      within(tables[1])
        .getAllByRole('row')
        .map(row =>
          within(row)
            .getAllByRole('cell')
            .map(cell => cell.textContent)
        )
    ).toEqual([
      ['Name', 'Andi Smith'],
      ['Gender', 'Male'],
      ['Country', 'Indonesia'],
      ['Home address', '123 Main St'],
      ['Shirt size', 'M'],
      ['Institution name', 'MIT'],
      ['Institution country', 'United States'],
      ['Institution province/state', 'Massachusetts'],
      ['Institution city', 'Cambridge'],
    ]);
  });
});
