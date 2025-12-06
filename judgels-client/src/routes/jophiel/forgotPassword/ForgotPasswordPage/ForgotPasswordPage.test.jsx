import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import ForgotPasswordPage from './ForgotPasswordPage';

import * as forgotPasswordActions from '../modules/forgotPasswordActions';

vi.mock('../modules/forgotPasswordActions');

describe('ForgotPasswordPage', () => {
  beforeEach(() => {
    forgotPasswordActions.requestToResetPassword.mockReturnValue(() => Promise.resolve());

    const store = configureMockStore([thunk])({});

    render(
      <Provider store={store}>
        <ForgotPasswordPage />
      </Provider>
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const email = screen.getByRole('textbox');
    await user.type(email, 'email@domain.com');

    const submitButton = screen.getByRole('button', { name: /request to reset password/i });
    await user.click(submitButton);

    expect(forgotPasswordActions.requestToResetPassword).toHaveBeenCalledWith('email@domain.com');
    expect(document.querySelector('[data-key="instruction"]')).toBeInTheDocument();
  });
});
