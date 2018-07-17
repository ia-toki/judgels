import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import createMockStore from 'redux-mock-store';

import { createLogoutContainer } from './Logout';

describe('LogoutContainer', () => {
  let logoutActions: jest.Mocked<any>;

  beforeEach(() => {
    logoutActions = {
      logOut: jest.fn().mockReturnValue({ type: 'mock-logout' }),
    };

    const store = createMockStore()({});
    const LogoutContainer = createLogoutContainer(logoutActions);

    mount(
      <Provider store={store}>
        <MemoryRouter>
          <LogoutContainer />
        </MemoryRouter>
      </Provider>
    );
  });

  it('logs out immediately', () => {
    expect(logoutActions.logOut).toHaveBeenCalled();
  });
});
