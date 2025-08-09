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
    name.prop('onChange')({ target: { value: 'My New Name' } });

    const gender = wrapper.find('select[name="gender"]');
    gender.prop('onChange')({ target: { value: 'FEMALE' } });

    const country = wrapper.find('select[name="country"]');
    country.prop('onChange')({ target: { value: 'SG' } });

    const homeAddress = wrapper.find('textarea[name="homeAddress"]');
    homeAddress.prop('onChange')({ target: { value: 'My New Address' } });

    const shirtSize = wrapper.find('select[name="shirtSize"]');
    shirtSize.prop('onChange')({ target: { value: 'S' } });

    const institutionName = wrapper.find('input[name="institutionName"]');
    institutionName.prop('onChange')({ target: { value: 'My New Institution' } });

    const institutionCountry = wrapper.find('select[name="institutionCountry"]');
    institutionCountry.prop('onChange')({ target: { value: 'US' } });

    const institutionProvince = wrapper.find('input[name="institutionProvince"]');
    institutionProvince.prop('onChange')({ target: { value: 'My New Province' } });

    const institutionCity = wrapper.find('input[name="institutionCity"]');
    institutionCity.prop('onChange')({ target: { value: 'My New City' } });

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
