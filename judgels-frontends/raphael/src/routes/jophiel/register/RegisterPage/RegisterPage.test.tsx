import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { AppState } from '../../../../modules/store';

import { createRegisterPage } from './RegisterPage';
import RegisterForm from '../RegisterForm/RegisterForm';
import { jophielReducer } from '../../modules/jophielReducer';

describe('RegisterPage', () => {
  let registerActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    registerActions = {
      getWebConfig: jest.fn().mockReturnValue(() => Promise.resolve({ useRecaptcha: false })),
      registerUser: jest.fn().mockReturnValue(() => Promise.resolve({})),
    };

    const store: any = createStore<Partial<AppState>>(
      combineReducers({
        form: formReducer,
        jophiel: jophielReducer,
      }),
      applyMiddleware(thunk)
    );

    const RegisterPage = createRegisterPage(registerActions);

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <RegisterPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('register form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const username = wrapper.find('input[name="username"]');
    username.simulate('change', { target: { value: 'user' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'name' } });

    const email = wrapper.find('input[name="email"]');
    email.simulate('change', { target: { value: 'email@domain.com' } });

    const password = wrapper.find('input[name="password"]');
    password.simulate('change', { target: { value: 'pass' } });

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.simulate('change', { target: { value: 'pass' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(registerActions.registerUser.mock.calls[0][0]).toEqual({
      username: 'user',
      name: 'name',
      email: 'email@domain.com',
      password: 'pass',
    });

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    expect(wrapper.find(RegisterForm)).toHaveLength(0);
    expect(wrapper.find('[data-key="instruction"]').text()).toContain('email@domain.com');
  });
});
