import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { createChangePasswordContainer } from './ChangePassword';

describe('ChangePasswordContainer', () => {
  let changePasswordActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    changePasswordActions = {
      changePassword: jest.fn().mockReturnValue({ type: 'mock-changePassword' }),
    };

    const store = createStore(combineReducers({ form: formReducer }));
    const ChangePasswordContainer = createChangePasswordContainer(changePasswordActions);

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ChangePasswordContainer />
        </MemoryRouter>
      </Provider>
    );
  });

  it('has working change password form', () => {
    const oldPassword = wrapper.find('input[name="oldPassword"]');
    oldPassword.simulate('change', { target: { value: 'oldPass' } });

    const password = wrapper.find('input[name="password"]');
    password.simulate('change', { target: { value: 'newPass' } });

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.simulate('change', { target: { value: 'newPass' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(changePasswordActions.changePassword).toHaveBeenCalledWith('oldPass', 'newPass');
  });
});
