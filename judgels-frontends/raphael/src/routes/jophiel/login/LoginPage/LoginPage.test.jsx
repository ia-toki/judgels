import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import createMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import LoginPage from './LoginPage';
import * as loginActions from '../modules/loginActions';

jest.mock('../modules/loginActions');

describe('LoginPage', () => {
  let wrapper;

  beforeEach(() => {
    loginActions.logIn.mockReturnValue(() => Promise.resolve());

    const store = createMockStore([thunk])({});

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <LoginPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('form', () => {
    const usernameOrEmail = wrapper.find('input[name="usernameOrEmail"]');
    usernameOrEmail.getDOMNode().value = 'user';
    usernameOrEmail.simulate('input');

    const password = wrapper.find('input[name="password"]');
    password.getDOMNode().value = 'pass';
    password.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(loginActions.logIn).toHaveBeenCalled();
  });
});
