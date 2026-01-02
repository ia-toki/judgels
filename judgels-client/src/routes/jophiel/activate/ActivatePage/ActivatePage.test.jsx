import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import ActivatePage from './ActivatePage';

import * as activateActions from '../modules/activateActions';

vi.mock('../modules/activateActions');

describe('ActivatePage', () => {
  let store;

  beforeEach(() => {
    activateActions.activateUser.mockReturnValue(() => Promise.resolve());

    store = createStore(() => {}, applyMiddleware(thunk));

    render(
      <Provider store={store}>
        <TestRouter initialEntries={['/activate/code123']} path="/activate/$emailCode">
          <ActivatePage />
        </TestRouter>
      </Provider>
    );
  });

  test('activate', () => {
    expect(activateActions.activateUser).toHaveBeenCalledWith('code123');
  });
});
