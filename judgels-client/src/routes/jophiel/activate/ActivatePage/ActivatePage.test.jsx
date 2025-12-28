import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';
import { vi } from 'vitest';

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
        <MemoryRouter initialEntries={['/activate/code123']}>
          <Routes>
            <Route path="/activate/:emailCode" element={<ActivatePage />} />
          </Routes>
        </MemoryRouter>
      </Provider>
    );
  });

  test('activate', () => {
    expect(activateActions.activateUser).toHaveBeenCalledWith('code123');
  });
});
