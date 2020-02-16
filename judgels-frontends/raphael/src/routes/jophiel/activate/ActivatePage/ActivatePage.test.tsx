import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ActivatePage from './ActivatePage';
import * as activateActions from '../modules/activateActions';

jest.mock('../modules/activateActions');

describe('ActivatePage', () => {
  let store: any;

  beforeEach(() => {
    (activateActions.activateUser as jest.Mock).mockReturnValue(() => Promise.resolve());

    store = createStore(() => {}, applyMiddleware(thunk));

    mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/activate/code123']}>
          <Route exact path="/activate/:emailCode" component={ActivatePage} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('activate', () => {
    expect(activateActions.activateUser).toHaveBeenCalledWith('code123');
  });
});
