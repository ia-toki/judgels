import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore, Store } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { UserInfo } from '../../../../../modules/api/jophiel/userInfo';
import { AppState } from '../../../../../modules/store';
import { InfoPanel } from './InfoPanel';

describe('InfoPanel', () => {
  let onUpdateInfo: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpdateInfo = jest.fn().mockReturnValue({ type: 'mock-update', then: fn => fn() });

    const info: UserInfo = {
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

    const store: Store<Partial<AppState>> = createStore(combineReducers({ form: formReducer }));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <InfoPanel info={info} onUpdateInfo={onUpdateInfo} />
        </MemoryRouter>
      </Provider>
    );
  });

  it('has working info form', async () => {
    expect(wrapper.find('[data-key="name"]').text()).toEqual('My Name');
    expect(wrapper.find('[data-key="gender"]').text()).toEqual('Male');
    expect(wrapper.find('[data-key="country"]').text()).toEqual('Indonesia');
    expect(wrapper.find('[data-key="homeAddress"]').text()).toEqual('My Address');
    expect(wrapper.find('[data-key="shirtSize"]').text()).toEqual('XL');
    expect(wrapper.find('[data-key="institutionName"]').text()).toEqual('My Institution');
    expect(wrapper.find('[data-key="institutionCountry"]').text()).toEqual('United Kingdom');
    expect(wrapper.find('[data-key="institutionProvince"]').text()).toEqual('My Province');
    expect(wrapper.find('[data-key="institutionCity"]').text()).toEqual('My City');

    wrapper.find('button[data-key="edit"]').simulate('click');

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'My New Name' } });

    const gender = wrapper.find('select[name="gender"]');
    gender.simulate('change', { target: { value: 'FEMALE' } });

    const country = wrapper.find('select[name="country"]');
    country.simulate('change', { target: { value: 'SG' } });

    const homeAddress = wrapper.find('textarea[name="homeAddress"]');
    homeAddress.simulate('change', { target: { value: 'My New Address' } });

    const shirtSize = wrapper.find('select[name="shirtSize"]');
    shirtSize.simulate('change', { target: { value: 'S' } });

    const institutionName = wrapper.find('input[name="institutionName"]');
    institutionName.simulate('change', { target: { value: 'My New Institution' } });

    const institutionCountry = wrapper.find('select[name="institutionCountry"]');
    institutionCountry.simulate('change', { target: { value: 'United States' } });

    const institutionProvince = wrapper.find('input[name="institutionProvince"]');
    institutionProvince.simulate('change', {
      target: { value: 'My New Province' },
    });

    const institutionCity = wrapper.find('input[name="institutionCity"]');
    institutionCity.simulate('change', { target: { value: 'My New City' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateInfo).toHaveBeenCalledWith({
      name: 'My New Name',
      gender: 'FEMALE',
      country: 'SG',
      homeAddress: 'My New Address',
      shirtSize: 'S',
      institutionName: 'My New Institution',
      institutionCountry: 'United States',
      institutionProvince: 'My New Province',
      institutionCity: 'My New City',
    });
  });
});
