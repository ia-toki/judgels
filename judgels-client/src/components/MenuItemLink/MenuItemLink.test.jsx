import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { push } from 'connected-react-router';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import MenuItemLink from './MenuItemLink';

describe('MenuItemLink', () => {
  let store;

  beforeEach(() => {
    store = createMockStore()({});
    render(
      <Provider store={store}>
        <MenuItemLink text="Account" to="/account" />
      </Provider>
    );
  });

  it('shows the text', () => {
    expect(screen.getByText('Account')).toBeInTheDocument();
  });

  it('pushes new location when clicked', async () => {
    const user = userEvent.setup();
    const link = screen.getByRole('menuitem', { name: /Account/i });
    await user.click(link);
    expect(store.getActions()).toContainEqual(push('/account'));
  });
});
