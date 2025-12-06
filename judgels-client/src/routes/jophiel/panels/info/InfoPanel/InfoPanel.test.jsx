import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import { vi } from 'vitest';

import { InfoPanel } from './InfoPanel';

describe('InfoPanel', () => {
  let onUpdateInfo;

  beforeEach(() => {
    onUpdateInfo = vi.fn().mockReturnValue({ type: 'mock-update', then: fn => fn() });

    const info = {
      name: 'My Name',
      gender: 'MALE',
      country: 'ID',
      homeAddress: 'My Address',
      shirtSize: 'XL',
      institutionName: 'My Institution',
      institutionCountry: 'GB',
      institutionProvince: 'My Province',
      institutionCity: 'My City',
    };

    const store = configureMockStore()({});

    render(
      <Provider store={store}>
        <InfoPanel email="user@domain.com" info={info} onUpdateInfo={onUpdateInfo} />
      </Provider>
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    expect(document.querySelector('[data-key="name"]').textContent).toEqual('My Name');
    expect(document.querySelector('[data-key="gender"]').textContent).toEqual('Male');
    expect(document.querySelector('[data-key="country"]').textContent).toEqual('Indonesia');
    expect(document.querySelector('[data-key="homeAddress"]').textContent).toEqual('My Address');
    expect(document.querySelector('[data-key="shirtSize"]').textContent).toEqual('XL');
    expect(document.querySelector('[data-key="institutionName"]').textContent).toEqual('My Institution');
    expect(document.querySelector('[data-key="institutionCountry"]').textContent).toEqual('United Kingdom');
    expect(document.querySelector('[data-key="institutionProvince"]').textContent).toEqual('My Province');
    expect(document.querySelector('[data-key="institutionCity"]').textContent).toEqual('My City');

    const editButton = screen.getByRole('button', { name: /edit/i });
    await user.click(editButton);

    const name = screen.getByDisplayValue('My Name');
    await user.clear(name);
    await user.type(name, 'My New Name');

    const gender = screen.getByDisplayValue('Male');
    await user.selectOptions(gender, 'FEMALE');

    const country = screen.getByDisplayValue('Indonesia');
    await user.selectOptions(country, 'SG');

    const homeAddress = screen.getByDisplayValue('My Address');
    await user.clear(homeAddress);
    await user.type(homeAddress, 'My New Address');

    const shirtSize = screen.getByDisplayValue('XL');
    await user.selectOptions(shirtSize, 'S');

    const institutionName = screen.getByDisplayValue('My Institution');
    await user.clear(institutionName);
    await user.type(institutionName, 'My New Institution');

    const institutionCountry = screen.getByDisplayValue('United Kingdom');
    await user.selectOptions(institutionCountry, 'US');

    const institutionProvince = screen.getByDisplayValue('My Province');
    await user.clear(institutionProvince);
    await user.type(institutionProvince, 'My New Province');

    const institutionCity = screen.getByDisplayValue('My City');
    await user.clear(institutionCity);
    await user.type(institutionCity, 'My New City');

    const submitButton = screen.getByRole('button', { name: /save changes/i });
    await user.click(submitButton);

    expect(onUpdateInfo).toHaveBeenCalledWith({
      name: 'My New Name',
      gender: 'FEMALE',
      country: 'SG',
      homeAddress: 'My New Address',
      shirtSize: 'S',
      institutionName: 'My New Institution',
      institutionCountry: 'US',
      institutionProvince: 'My New Province',
      institutionCity: 'My New City',
    });
  });
});
