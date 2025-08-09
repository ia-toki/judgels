import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import ForgotPasswordPage from './ForgotPasswordPage';

import * as forgotPasswordActions from '../modules/forgotPasswordActions';

jest.mock('../modules/forgotPasswordActions');

describe('ForgotPasswordPage', () => {
  let wrapper;

  beforeEach(() => {
    forgotPasswordActions.requestToResetPassword.mockReturnValue(() => Promise.resolve());

    const store = configureMockStore([thunk])({});

    wrapper = mount(
      <Provider store={store}>
        <ForgotPasswordPage />
      </Provider>
    );
  });

  test('form', async () => {
    const email = wrapper.find('input[name="email"]');
    email.prop('onChange')({ target: { value: 'email@domain.com' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    expect(forgotPasswordActions.requestToResetPassword).toHaveBeenCalledWith('email@domain.com');
    expect(wrapper.find('[data-key="instruction"]')).toHaveLength(1);
  });
});
