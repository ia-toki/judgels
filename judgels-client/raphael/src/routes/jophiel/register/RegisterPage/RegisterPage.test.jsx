import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import RegisterPage from './RegisterPage';
import RegisterForm from '../RegisterForm/RegisterForm';
import jophielReducer from '../../../../modules/jophiel/jophielReducer';
import * as registerActions from '../modules/registerActions';

jest.mock('../modules/registerActions');

describe('RegisterPage', () => {
  let wrapper;

  beforeEach(() => {
    registerActions.getWebConfig.mockReturnValue(() => Promise.resolve({ useRecaptcha: false }));
    registerActions.registerUser.mockReturnValue(() => Promise.resolve());

    const store = createStore(
      combineReducers({
        jophiel: jophielReducer,
      }),
      applyMiddleware(thunk)
    );

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
    username.getDOMNode().value = 'user';
    username.simulate('input');

    const name = wrapper.find('input[name="name"]');
    name.getDOMNode().value = 'name';
    name.simulate('input');

    const email = wrapper.find('input[name="email"]');
    email.getDOMNode().value = 'email@domain.com';
    email.simulate('input');

    const password = wrapper.find('input[name="password"]');
    password.getDOMNode().value = 'pass';
    password.simulate('input');

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.getDOMNode().value = 'pass';
    confirmPassword.simulate('input');

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
