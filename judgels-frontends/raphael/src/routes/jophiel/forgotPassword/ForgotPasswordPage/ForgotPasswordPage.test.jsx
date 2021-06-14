import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import ForgotPasswordPage from './ForgotPasswordPage';
import * as forgotPasswordActions from '../modules/forgotPasswordActions';

jest.mock('../modules/forgotPasswordActions');

describe('ForgotPasswordPage', () => {
  let wrapper;

  beforeEach(() => {
    forgotPasswordActions.requestToResetPassword.mockReturnValue(() => Promise.resolve());

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    wrapper = mount(
      <Provider store={store}>
        <ForgotPasswordPage />
      </Provider>
    );
  });

  test('form', async () => {
    const email = wrapper.find('input[name="email"]');
    email.getDOMNode().value = 'email@domain.com';
    email.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    expect(forgotPasswordActions.requestToResetPassword).toHaveBeenCalledWith('email@domain.com');
    expect(wrapper.find('[data-key="instruction"]')).toHaveLength(1);
  });
});
