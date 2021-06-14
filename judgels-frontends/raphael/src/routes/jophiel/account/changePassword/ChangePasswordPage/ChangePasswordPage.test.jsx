import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import ChangePasswordPage from './ChangePasswordPage';
import * as changePasswordActions from '../modules/changePasswordActions';

jest.mock('../modules/changePasswordActions');

describe('ChangePasswordPage', () => {
  let wrapper;

  beforeEach(() => {
    changePasswordActions.updateMyPassword.mockReturnValue(() => Promise.resolve());

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ChangePasswordPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('form', () => {
    const oldPassword = wrapper.find('input[name="oldPassword"]');
    oldPassword.getDOMNode().value = 'oldPass';
    oldPassword.simulate('input');

    const password = wrapper.find('input[name="password"]');
    password.getDOMNode().value = 'newPass';
    password.simulate('input');

    const confirmPassword = wrapper.find('input[name="confirmPassword"]');
    confirmPassword.getDOMNode().value = 'newPass';
    confirmPassword.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(changePasswordActions.updateMyPassword).toHaveBeenCalledWith('oldPass', 'newPass');
  });
});
