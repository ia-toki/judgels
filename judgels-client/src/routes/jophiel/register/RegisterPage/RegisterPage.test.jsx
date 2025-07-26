import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';

import RegisterForm from '../RegisterForm/RegisterForm';
import RegisterPage from './RegisterPage';

import * as registerActions from '../modules/registerActions';

jest.mock('../modules/registerActions');

describe('RegisterPage', () => {
  let wrapper;

  beforeEach(() => {
    registerActions.getWebConfig.mockReturnValue(() => Promise.resolve({ useRecaptcha: false }));
    registerActions.registerUser.mockReturnValue(() => Promise.resolve());

    const store = createStore(() => {}, applyMiddleware(thunk));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <RegisterPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const username = wrapper.find('input[name="username"]');
    username.prop('onChange')({ target: { value: 'user' } });

    const name = wrapper.find('input[name="name"]');
    name.prop('onChange')({ target: { value: 'name' } });

    const email = wrapper.find('input[name="email"]');
    email.prop('onChange')({ target: { value: 'email@domain.com' } });

    const password = wrapper.find('input[name="password"]');
    password.prop('onChange')({ target: { value: 'pass' } });

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.prop('onChange')({ target: { value: 'pass' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(registerActions.registerUser).toHaveBeenCalledWith({
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
