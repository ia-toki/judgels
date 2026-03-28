import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';
import { Suspense } from 'react';

import { setSession } from '../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../test/QueryClientProviderWrapper';
import { nockJophiel } from '../../../../utils/nock';
import { UserInfoSection } from './UserInfoSection';

describe('UserInfoSection', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const user = {
    jid: 'JIDUSER123',
    username: 'andi',
    email: 'andi@example.com',
  };

  const renderComponent = async () => {
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
          <Suspense>
            <UserInfoSection user={user} />
          </Suspense>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('renders info details', async () => {
    await renderComponent();

    const table = await screen.findByRole('table');
    const rows = screen.getAllByRole('row');
    expect(rows.map(row => screen.getAllByRole('cell', { container: row }).map(cell => cell.textContent)));
  });

  test('form', async () => {
    await renderComponent();

    const u = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await u.click(button);

    const names = screen.getAllByRole('textbox', { name: /name/i });
    expect(names[0]).toHaveValue('Andi Smith');
    await u.clear(names[0]);
    await u.type(names[0], 'Caca');

    const gender = screen.getByRole('combobox', { name: /gender/i });
    expect(gender).toHaveValue('MALE');
    await u.selectOptions(gender, 'FEMALE');

    const countries = screen.getAllByRole('combobox', { name: /country/i });
    expect(countries[0]).toHaveValue('ID');
    await u.selectOptions(countries[0], 'US');

    const homeAddress = document.querySelector('textarea[name="homeAddress"]');
    expect(homeAddress).toHaveValue('123 Main St');
    await u.clear(homeAddress);
    await u.type(homeAddress, '456 Oak Ave');

    const shirtSize = screen.getByRole('combobox', { name: /shirt size/i });
    expect(shirtSize).toHaveValue('M');
    await u.selectOptions(shirtSize, 'L');

    expect(names[1]).toHaveValue('MIT');
    await u.clear(names[1]);
    await u.type(names[1], 'Stanford');

    expect(countries[1]).toHaveValue('US');
    await u.selectOptions(countries[1], 'GB');

    const institutionProvince = screen.getByRole('textbox', { name: /institution province/i });
    expect(institutionProvince).toHaveValue('Massachusetts');
    await u.clear(institutionProvince);
    await u.type(institutionProvince, 'England');

    const institutionCity = screen.getByRole('textbox', { name: /institution city/i });
    expect(institutionCity).toHaveValue('Cambridge');
    await u.clear(institutionCity);
    await u.type(institutionCity, 'London');

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

    await u.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
