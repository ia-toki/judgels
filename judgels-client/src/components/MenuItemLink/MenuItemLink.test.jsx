import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router';

import MenuItemLink from './MenuItemLink';

describe('MenuItemLink', () => {
  beforeEach(() => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <Routes>
          <Route path="/" element={<MenuItemLink text="Account" to="/account" />} />
          <Route path="/account" element={<div>Account Page</div>} />
        </Routes>
      </MemoryRouter>
    );
  });

  it('shows the text', () => {
    expect(screen.getByText('Account')).toBeInTheDocument();
  });

  it('navigates when clicked', async () => {
    const user = userEvent.setup();
    const link = screen.getByRole('menuitem', { name: /Account/i });
    await user.click(link);
    expect(screen.getByText('Account Page')).toBeInTheDocument();
  });
});
