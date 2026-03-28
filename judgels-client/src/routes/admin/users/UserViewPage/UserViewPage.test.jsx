import { act, render, screen, waitFor, within } from '@testing-library/react';
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
            <UserViewPage />
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

  test('user info form', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const names = screen.getAllByRole('textbox', { name: /name/i });
    expect(names[0]).toHaveValue('Andi Smith');
    await user.clear(names[0]);
    await user.type(names[0], 'Caca');

    const gender = screen.getByRole('combobox', { name: /gender/i });
    expect(gender).toHaveValue('MALE');
    await user.selectOptions(gender, 'FEMALE');

    const countries = screen.getAllByRole('combobox', { name: /country/i });
    expect(countries[0]).toHaveValue('ID');
    await user.selectOptions(countries[0], 'US');

    const homeAddress = document.querySelector('textarea[name="homeAddress"]');
    expect(homeAddress).toHaveValue('123 Main St');
    await user.clear(homeAddress);
    await user.type(homeAddress, '456 Oak Ave');

    const shirtSize = screen.getByRole('combobox', { name: /shirt size/i });
    expect(shirtSize).toHaveValue('M');
    await user.selectOptions(shirtSize, 'L');

    expect(names[1]).toHaveValue('MIT');
    await user.clear(names[1]);
    await user.type(names[1], 'Stanford');

    expect(countries[1]).toHaveValue('US');
    await user.selectOptions(countries[1], 'GB');

    const institutionProvince = screen.getByRole('textbox', { name: /institution province/i });
    expect(institutionProvince).toHaveValue('Massachusetts');
    await user.clear(institutionProvince);
    await user.type(institutionProvince, 'England');

    const institutionCity = screen.getByRole('textbox', { name: /institution city/i });
    expect(institutionCity).toHaveValue('Cambridge');
    await user.clear(institutionCity);
    await user.type(institutionCity, 'London');

    nockJophiel()
      .put('/users/JIDUSER123/info', {
        name: 'Caca',
        gender: 'FEMALE',
        country: 'US',
        homeAddress: '456 Oak Ave',
        shirtSize: 'L',
        institutionName: 'Stanford',
        institutionCountry: 'GB',
        institutionProvince: 'England',
        institutionCity: 'London',
      })
      .reply(200, {
        name: 'Caca',
        gender: 'FEMALE',
        country: 'US',
        homeAddress: '456 Oak Ave',
        shirtSize: 'L',
        institutionName: 'Stanford',
        institutionCountry: 'GB',
        institutionProvince: 'England',
        institutionCity: 'London',
      });

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
