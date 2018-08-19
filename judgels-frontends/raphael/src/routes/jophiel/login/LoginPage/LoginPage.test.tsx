import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { createLoginPage } from './LoginPage';

describe('LoginPage', () => {
  let loginActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    loginActions = {
      logIn: jest.fn().mockReturnValue(() => Promise.resolve({})),
    };

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));
    const LoginPage = createLoginPage(loginActions);

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <LoginPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('login form', () => {
    const usernameOrEmail = wrapper.find('input[name="usernameOrEmail"]');
    usernameOrEmail.simulate('change', { target: { value: 'user' } });

    const password = wrapper.find('input[name="password"]');
    password.simulate('change', { target: { value: 'pass' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(loginActions.logIn).toHaveBeenCalled();
  });
});
