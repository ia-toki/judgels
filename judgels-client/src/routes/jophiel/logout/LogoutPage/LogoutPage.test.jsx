import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import LogoutPage from './LogoutPage';

import * as logoutActions from '../modules/logoutActions';

vi.mock('../modules/logoutActions');

describe('LogoutPage', () => {
  beforeEach(() => {
    logoutActions.logOut.mockReturnValue(() => Promise.resolve());

    const store = createStore(() => {}, applyMiddleware(thunk));

    render(
      <Provider store={store}>
        <TestRouter>
          <LogoutPage />
        </TestRouter>
      </Provider>
    );
  });

  it('logs out immediately', () => {
    expect(logoutActions.logOut).toHaveBeenCalled();
  });
});
