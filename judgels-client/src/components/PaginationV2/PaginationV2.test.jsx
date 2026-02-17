import { RouterProvider, useLocation } from '@tanstack/react-router';
import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { createTestRouter } from '../../test/RouterWrapper';
import PaginationV2 from './PaginationV2';

describe('PaginationV2', () => {
  let testLocation;

  const LocationTracker = () => {
    const location = useLocation();
    testLocation = location;
    return null;
  };

  const renderComponent = (totalCount, pageQuery) => {
    const router = createTestRouter(
      () => (
        <>
          <PaginationV2 pageSize={6} totalCount={totalCount} />
          <LocationTracker />
        </>
      ),
      ['/component' + pageQuery]
    );

    render(<RouterProvider router={router} />);
  };

  describe('when totalCount is 0', () => {
    it('does not show the helper text', async () => {
      await act(async () => {
        renderComponent(0, '');
      });
      expect(screen.queryByText(/Showing.*results/i)).not.toBeInTheDocument();
    });
  });

  describe('when on page 1', () => {
    it('shows the helper text', async () => {
      await act(async () => {
        renderComponent(14, '');
      });
      expect(await screen.findByText(/Showing 1\.\.6 of 14 results/i)).toBeInTheDocument();
    });
  });

  describe('when on page 3', () => {
    it('shows the helper text', async () => {
      await act(async () => {
        renderComponent(14, '?page=3');
      });
      expect(await screen.findByText(/Showing 13\.\.14 of 14 results/i)).toBeInTheDocument();
    });
  });

  describe('when page changes', () => {
    it('navigates URL correctly', async () => {
      await act(async () => {
        renderComponent(14, '');
      });

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
});
