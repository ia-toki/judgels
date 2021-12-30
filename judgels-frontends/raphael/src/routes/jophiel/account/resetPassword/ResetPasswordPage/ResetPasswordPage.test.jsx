import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import ResetPasswordPage from './ResetPasswordPage';
import * as resetPasswordActions from '../modules/resetPasswordActions';

jest.mock('../modules/resetPasswordActions');

describe('ResetPasswordPage', () => {
  let wrapper;

  beforeEach(() => {
    resetPasswordActions.requestToResetPassword.mockReturnValue(() => Promise.resolve());

    const store = configureMockStore([thunk])({});

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ResetPasswordPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('form', () => {
    const form = wrapper.find('form');
    form.simulate('submit');

    expect(resetPasswordActions.requestToResetPassword).toHaveBeenCalled();
  });
});
