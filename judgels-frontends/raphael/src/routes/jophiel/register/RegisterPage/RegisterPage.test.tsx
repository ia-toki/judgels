import { mount, ReactWrapper, shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { reducer as formReducer } from 'redux-form';

import { createRegisterPage, RegisterPage, RegisterPageProps } from './RegisterPage';
import RegisterForm from '../RegisterForm/RegisterForm';
import { combineReducers, createStore } from 'redux';
import { AppState } from '../../../../modules/store';
import { jophielReducer } from '../../modules/jophielReducer';

describe('RegisterPageShallow', () => {
  let wrapper: ShallowWrapper;

  let onRegisterUser: jest.Mock<any>;

  const render = () => {
    const props: RegisterPageProps = {
      useRecaptcha: false,
      onRegisterUser,
    };

    wrapper = shallow(<RegisterPage {...props} />);
  };

  beforeEach(() => {
    onRegisterUser = jest.fn();
    render();
  });

  it('shows the activation page after registration', async () => {
    const form = wrapper.find(RegisterForm);
    expect(form).toHaveLength(1);

    (form.props().onSubmit as any)({
      username: 'user',
      email: 'email@domain.com',
    });

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    expect(wrapper.find(RegisterForm)).toHaveLength(0);
    expect(wrapper.find('[data-key="instruction"]').text()).toContain('email@domain.com');
  });
});

describe('RegisterPage', () => {
  let registerActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    registerActions = {
      registerUser: jest.fn().mockReturnValue({ type: 'mock-register', then: fn => fn() }),
    };

    const store = createStore<Partial<AppState>>(
      combineReducers({
        form: formReducer,
        jophiel: jophielReducer,
      })
    );

    const RegisterPageLocal = createRegisterPage(registerActions);

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <RegisterPageLocal />
        </MemoryRouter>
      </Provider>
    );
  });

  test('register form', () => {
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
  });
});
