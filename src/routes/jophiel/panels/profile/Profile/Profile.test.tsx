import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore, Store } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { UserProfile } from '../../../../../modules/api/jophiel/user';
import { AppState } from '../../../../../modules/store';
import { ProfilePanel } from './Profile';

describe('ProfilePanel', () => {
  let onUpdateProfile: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpdateProfile = jest.fn().mockReturnValue({ type: 'mock-update', then: fn => fn() });

    const profile: UserProfile = {
      name: 'My Name',
      gender: 'MALE',
      nationality: 'Indonesia',
      homeAddress: 'My Address',
      shirtSize: 'XL',
      institution: 'My Institution',
      country: 'United Kingdom',
      province: 'My Province',
      city: 'My City',
    };

    const store: Store<Partial<AppState>> = createStore(combineReducers({ form: formReducer }));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ProfilePanel profile={profile} onUpdateProfile={onUpdateProfile} />
        </MemoryRouter>
      </Provider>
    );
  });

  it('has working profile form', async () => {
    expect(wrapper.find('[data-key="name"]').text()).toEqual('My Name');
    expect(wrapper.find('[data-key="gender"]').text()).toEqual('Male');
    expect(wrapper.find('[data-key="nationality"]').text()).toEqual('Indonesia');
    expect(wrapper.find('[data-key="homeAddress"]').text()).toEqual('My Address');
    expect(wrapper.find('[data-key="shirtSize"]').text()).toEqual('XL');
    expect(wrapper.find('[data-key="institution"]').text()).toEqual('My Institution');
    expect(wrapper.find('[data-key="country"]').text()).toEqual('United Kingdom');
    expect(wrapper.find('[data-key="province"]').text()).toEqual('My Province');
    expect(wrapper.find('[data-key="city"]').text()).toEqual('My City');

    wrapper.find('button[data-key="edit"]').simulate('click');

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'My New Name' } });

    const gender = wrapper.find('select[name="gender"]');
    gender.simulate('change', { target: { value: 'FEMALE' } });

    const nationality = wrapper.find('select[name="nationality"]');
    nationality.simulate('change', { target: { value: 'Singapore' } });

    const homeAddress = wrapper.find('textarea[name="homeAddress"]');
    homeAddress.simulate('change', { target: { value: 'My New Address' } });

    const shirtSize = wrapper.find('select[name="shirtSize"]');
    shirtSize.simulate('change', { target: { value: 'S' } });

    const institution = wrapper.find('input[name="institution"]');
    institution.simulate('change', { target: { value: 'My New Institution' } });

    const country = wrapper.find('select[name="country"]');
    country.simulate('change', { target: { value: 'United States' } });

    const province = wrapper.find('input[name="province"]');
    province.simulate('change', {
      target: { value: 'My New Province' },
    });

    const city = wrapper.find('input[name="city"]');
    city.simulate('change', { target: { value: 'My New City' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateProfile).toHaveBeenCalledWith({
      name: 'My New Name',
      gender: 'FEMALE',
      nationality: 'Singapore',
      homeAddress: 'My New Address',
      shirtSize: 'S',
      institution: 'My New Institution',
      country: 'United States',
      province: 'My New Province',
      city: 'My New City',
    });
  });
});
