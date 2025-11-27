import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';

import ActivatePage from './ActivatePage';

import * as activateActions from '../modules/activateActions';

jest.mock('../modules/activateActions');

describe('ActivatePage', () => {
  let store;

  beforeEach(() => {
    activateActions.activateUser.mockReturnValue(() => Promise.resolve());

    store = createStore(() => {}, applyMiddleware(thunk));

    render(
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
