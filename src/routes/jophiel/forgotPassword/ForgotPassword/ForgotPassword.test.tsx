import { mount, ReactWrapper, shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { createForgotPasswordContainer, ForgotPassword, ForgotPasswordProps } from './ForgotPassword';
import ForgotPasswordForm from '../ForgotPasswordForm/ForgotPasswordForm';

describe('ForgotPassword', () => {
  let wrapper: ShallowWrapper;

  let onForgetPassword: jest.Mock<any>;

  const render = () => {
    const props: ForgotPasswordProps = {
      onForgetPassword,
    };

    wrapper = shallow(<ForgotPassword {...props} />);
  };

  beforeEach(() => {
    onForgetPassword = jest.fn();
    render();
  });

  it('shows the instruction page after request', async () => {
    const form = wrapper.find(ForgotPasswordForm);
    expect(form).toHaveLength(1);

    (form.props().onSubmit as any)({ email: 'email@domain.com' });

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    expect(wrapper.find(ForgotPasswordForm)).toHaveLength(0);
    expect(wrapper.find('[data-key="instruction"]')).toHaveLength(1);
  });
});

describe('ForgotPasswordContainer', () => {
  let forgotPasswordActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    forgotPasswordActions = {
      requestToReset: jest.fn().mockReturnValue({ type: 'mock-requestToReset' }),
    };

    const store = createStore(combineReducers({ form: formReducer }));
    const ForgotPasswordContainer = createForgotPasswordContainer(forgotPasswordActions);

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ForgotPasswordContainer />
        </MemoryRouter>
      </Provider>
    );
  });

  it('has working forgot password form', () => {
    const email = wrapper.find('input[name="email"]');
    email.simulate('change', { target: { value: 'email@domain.com' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(forgotPasswordActions.requestToReset).toHaveBeenCalledWith('email@domain.com');
  });
});
