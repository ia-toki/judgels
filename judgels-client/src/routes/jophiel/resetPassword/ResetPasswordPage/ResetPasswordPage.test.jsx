import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import ResetPasswordPage from './ResetPasswordPage';

import * as resetPasswordActions from '../modules/resetPasswordActions';

jest.mock('../modules/resetPasswordActions');

describe('ResetPasswordPage', () => {
  let wrapper;
  beforeEach(() => {
    resetPasswordActions.resetPassword.mockReturnValue(() => Promise.resolve());

    const store = configureMockStore([thunk])({});

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/reset-password/code123']}>
          <Route exact path="/reset-password/:emailCode" component={ResetPasswordPage} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('form', () => {
    const password = wrapper.find('input[name="password"]');
    password.getDOMNode().value = 'pass';
    password.simulate('input');

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.getDOMNode().value = 'pass';
    confirmPassword.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(resetPasswordActions.resetPassword).toHaveBeenCalledWith('code123', 'pass');
  });
});
