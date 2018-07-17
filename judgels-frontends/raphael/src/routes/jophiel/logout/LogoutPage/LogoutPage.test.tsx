import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import createMockStore from 'redux-mock-store';

import { createLogoutPage } from './LogoutPage';

describe('LogoutPage', () => {
  let logoutActions: jest.Mocked<any>;

  beforeEach(() => {
    logoutActions = {
      logOut: jest.fn().mockReturnValue({ type: 'mock-logout' }),
    };

    const store = createMockStore()({});
    const LogoutPage = createLogoutPage(logoutActions);

    mount(
      <Provider store={store}>
        <MemoryRouter>
          <LogoutPage />
        </MemoryRouter>
      </Provider>
    );
  });

  it('logs out immediately', () => {
    expect(logoutActions.logOut).toHaveBeenCalled();
  });
});
