import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import ForgotPasswordPage from './ForgotPasswordPage';
import * as forgotPasswordActions from '../modules/forgotPasswordActions';

jest.mock('../modules/forgotPasswordActions');

describe('ForgotPasswordPage', () => {
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    (forgotPasswordActions.requestToResetPassword as jest.Mock).mockReturnValue(() => Promise.resolve());

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ForgotPasswordPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('forgot password form', async () => {
    const email = wrapper.find('input[name="email"]');
    email.simulate('change', { target: { value: 'email@domain.com' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    expect(forgotPasswordActions.requestToResetPassword).toHaveBeenCalledWith('email@domain.com');
    expect(wrapper.find('[data-key="instruction"]')).toHaveLength(1);
  });
});
