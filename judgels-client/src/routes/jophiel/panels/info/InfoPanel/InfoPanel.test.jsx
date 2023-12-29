import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import configureMockStore from 'redux-mock-store';

import { InfoPanel } from './InfoPanel';

describe('InfoPanel', () => {
  let onUpdateInfo;
  let wrapper;

  beforeEach(() => {
    onUpdateInfo = jest.fn().mockReturnValue({ type: 'mock-update', then: fn => fn() });

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

    wrapper = mount(
      <Provider store={store}>
        <InfoPanel email="user@domain.com" info={info} onUpdateInfo={onUpdateInfo} />
      </Provider>
    );
  });

  test('form', async () => {
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
    name.getDOMNode().value = 'My New Name';
    name.simulate('input');

    const gender = wrapper.find('select[name="gender"]');
    gender.getDOMNode().value = 'FEMALE';
    gender.simulate('change');

    const country = wrapper.find('select[name="country"]');
    country.getDOMNode().value = 'SG';
    country.simulate('change');

    const homeAddress = wrapper.find('textarea[name="homeAddress"]');
    homeAddress.getDOMNode().value = 'My New Address';
    homeAddress.simulate('input');

    const shirtSize = wrapper.find('select[name="shirtSize"]');
    shirtSize.getDOMNode().value = 'S';
    shirtSize.simulate('change');

    const institutionName = wrapper.find('input[name="institutionName"]');
    institutionName.getDOMNode().value = 'My New Institution';
    institutionName.simulate('input');

    const institutionCountry = wrapper.find('select[name="institutionCountry"]');
    institutionCountry.getDOMNode().value = 'US';
    institutionCountry.simulate('change');

    const institutionProvince = wrapper.find('input[name="institutionProvince"]');
    institutionProvince.getDOMNode().value = 'My New Province';
    institutionProvince.simulate('input');

    const institutionCity = wrapper.find('input[name="institutionCity"]');
    institutionCity.getDOMNode().value = 'My New City';
    institutionCity.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

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
