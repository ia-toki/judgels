import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { createResetPasswordContainer } from './ResetPassword';

describe('ResetPasswordContainer', () => {
  let resetPasswordActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    resetPasswordActions = {
      reset: jest.fn().mockReturnValue({ type: 'mock-reset', then: fn => fn() }),
    };

    const store = createStore(combineReducers({ form: formReducer }));
    const ResetPasswordContainer = createResetPasswordContainer(resetPasswordActions);

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/reset-password/code123']}>
          <Route exact path="/reset-password/:emailCode" component={ResetPasswordContainer} />
        </MemoryRouter>
      </Provider>
    );
  });

  it('has working reset password form', () => {
    const password = wrapper.find('input[name="password"]');
    password.simulate('change', { target: { value: 'pass' } });

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.simulate('change', { target: { value: 'pass' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(resetPasswordActions.reset).toHaveBeenCalledWith('code123', 'pass');
  });
});
