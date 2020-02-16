import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import thunk from 'redux-thunk';

import ResetPasswordPage from './ResetPasswordPage';
import * as resetPasswordActions from '../modules/resetPasswordActions';

jest.mock('../modules/resetPasswordActions');

describe('ResetPasswordPage', () => {
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    (resetPasswordActions.resetPassword as jest.Mock).mockReturnValue(() => Promise.resolve());

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/reset-password/code123']}>
          <Route exact path="/reset-password/:emailCode" component={ResetPasswordPage} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('reset password form', () => {
    const password = wrapper.find('input[name="password"]');
    password.simulate('change', { target: { value: 'pass' } });

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.simulate('change', { target: { value: 'pass' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(resetPasswordActions.resetPassword).toHaveBeenCalledWith('code123', 'pass');
  });
});
