import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { createLoginContainer } from './Login';

describe('LoginContainer', () => {
  let loginActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    loginActions = {
      logIn: jest.fn().mockReturnValue({ type: 'mock-login' }),
    };

    const store = createStore(combineReducers({ form: formReducer }));
    const LoginContainer = createLoginContainer(loginActions);

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <LoginContainer />
        </MemoryRouter>
      </Provider>
    );
  });

  it('has working login form', () => {
    const username = wrapper.find('input[name="username"]');
    username.simulate('change', { target: { value: 'user' } });

    const password = wrapper.find('input[name="password"]');
    password.simulate('change', { target: { value: 'pass' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(loginActions.logIn).toHaveBeenCalled();
  });
});
