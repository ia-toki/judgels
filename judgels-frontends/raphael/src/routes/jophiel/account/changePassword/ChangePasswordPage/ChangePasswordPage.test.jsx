import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import ChangePasswordPage from './ChangePasswordPage';
import * as changePasswordActions from '../modules/changePasswordActions';

jest.mock('../modules/changePasswordActions');

describe('ChangePasswordPage', () => {
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    (changePasswordActions.updateMyPassword as jest.Mock).mockReturnValue(() => Promise.resolve());

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ChangePasswordPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('change password form', () => {
    const oldPassword = wrapper.find('input[name="oldPassword"]');
    oldPassword.simulate('change', { target: { value: 'oldPass' } });

    const password = wrapper.find('input[name="password"]');
    password.simulate('change', { target: { value: 'newPass' } });

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.simulate('change', { target: { value: 'newPass' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(changePasswordActions.updateMyPassword).toHaveBeenCalledWith('oldPass', 'newPass');
  });
});
