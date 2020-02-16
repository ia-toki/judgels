import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';

import LogoutPage from './LogoutPage';
import * as logoutActions from '../modules/logoutActions';

jest.mock('../modules/logoutActions');

describe('LogoutPage', () => {
  beforeEach(() => {
    (logoutActions.logOut as jest.Mock).mockReturnValue(() => Promise.resolve());

    const store: any = createStore(() => {}, applyMiddleware(thunk));

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
