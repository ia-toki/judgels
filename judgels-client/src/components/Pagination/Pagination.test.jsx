import { RouterProvider, useLocation } from '@tanstack/react-router';
import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { createTestRouter } from '../../test/RouterWrapper';
import Pagination from './Pagination';

describe('Pagination', () => {
  let testLocation;

  const LocationTracker = () => {
    const location = useLocation();
    testLocation = location;
    return null;
  };

  const renderComponent = async (totalCount, pageQuery) => {
    const router = createTestRouter(
      () => (
        <>
          <Pagination pageSize={6} totalCount={totalCount} />
          <LocationTracker />
        </>
      ),
      ['/component' + pageQuery]
    );

    await act(async () => render(<RouterProvider router={router} />));
  };

  test('when totalCount is 0, does not render the helper text', async () => {
    await renderComponent(0, '');
    expect(screen.queryByText(/Showing.*results/i)).not.toBeInTheDocument();
  });

  test('when on page 1, renders the helper text', async () => {
    await renderComponent(14, '');
    expect(await screen.findByText(/Showing 1\.\.6 of 14 results/i)).toBeInTheDocument();
  });

  test('when on page 3, renders the helper text', async () => {
    await renderComponent(14, '?page=3');
    expect(await screen.findByText(/Showing 13\.\.14 of 14 results/i)).toBeInTheDocument();
  });

  test('when page changes, navigates URL correctly', async () => {
    await renderComponent(14, '');

    const user = userEvent.setup();
    const pageButtons = screen.getAllByRole('button');
    const page2Button = pageButtons.find(btn => btn.textContent === '2');

    if (page2Button) {
      await user.click(page2Button);
      await waitFor(() => {
        expect(testLocation.search).toEqual({ page: '2' });
      });
    }
  });
});
