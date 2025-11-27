import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import ResetPasswordPage from './ResetPasswordPage';

import * as resetPasswordActions from '../modules/resetPasswordActions';

jest.mock('../modules/resetPasswordActions');

describe('ResetPasswordPage', () => {
  beforeEach(() => {
    resetPasswordActions.requestToResetPassword.mockReturnValue(() => Promise.resolve());

    const store = configureMockStore([thunk])({});

    render(
      <Provider store={store}>
        <MemoryRouter>
          <ResetPasswordPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const submitButton = screen.getByRole('button', { name: /request to reset password/i });
    await user.click(submitButton);

    expect(resetPasswordActions.requestToResetPassword).toHaveBeenCalled();
  });
});
