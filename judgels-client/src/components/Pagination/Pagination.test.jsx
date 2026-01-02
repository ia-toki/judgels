import { RouterProvider, useLocation } from '@tanstack/react-router';
import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { createTestRouter } from '../../test/RouterWrapper';
import Pagination from './Pagination';

describe('Pagination', () => {
  let onChangePage;
  let testLocation;

  const LocationTracker = () => {
    const location = useLocation();
    testLocation = location;
    return null;
  };

  const renderComponent = pageQuery => {
    const props = {
      pageSize: 6,
      onChangePage,
    };

    const router = createTestRouter(
      () => (
        <>
          <Pagination {...props} />
          <LocationTracker />
        </>
      ),
      ['/component' + pageQuery]
    );

    render(<RouterProvider router={router} />);
  };

  beforeEach(() => {
    onChangePage = vi.fn().mockReturnValue(Promise.resolve(14));
  });

  describe('when there is no data', () => {
    beforeEach(async () => {
      onChangePage = vi.fn().mockReturnValue(Promise.resolve(0));

      await act(async () => {
        renderComponent('');
      });
    });

    it('does not show the helper text', async () => {
      await waitFor(() => {
        expect(screen.queryByText(/Showing.*results/i)).not.toBeInTheDocument();
      });
    });
  });

  describe('when there is no page query string', () => {
    beforeEach(async () => {
      await act(async () => {
        renderComponent('');
      });
    });

    it('navigates to page 1', () => {
      expect(onChangePage).toBeCalledWith(1);
    });

    it('shows the helper text', async () => {
      expect(await screen.findByText(/Showing 1\.\.6 of 14 results/i)).toBeInTheDocument();
    });
  });

  describe('when there is page query string', () => {
    beforeEach(async () => {
      await act(async () => {
        renderComponent('?page=3');
      });
    });

    it('navigates to that page', () => {
      expect(onChangePage).toBeCalledWith(3);
    });

    it('shows the helper text', async () => {
      expect(await screen.findByText(/Showing 13\.\.14 of 14 results/i)).toBeInTheDocument();
    });
  });

  describe('when page changes', () => {
    beforeEach(async () => {
      await act(async () => {
        renderComponent('?page=2');
      });
    });

    describe('when page changes to page 1', () => {
      it('clears the query string', async () => {
        const user = userEvent.setup();

        const pageButtons = screen.getAllByRole('button');
        const page1Button = pageButtons.find(btn => btn.textContent === '1');

        if (page1Button) {
          await user.click(page1Button);
          await waitFor(() => {
            expect(testLocation.search).toEqual({});
          });
        }
      });
    });

    describe('when page changes to page > 1', () => {
      it('pushes the query string', async () => {
        const user = userEvent.setup();

        const pageButtons = screen.getAllByRole('button');
        const page3Button = pageButtons.find(btn => btn.textContent === '3');

        if (page3Button) {
          await user.click(page3Button);
          await waitFor(() => {
            expect(testLocation.search).toEqual({ page: '3' });
          });
        }
      });
    });
  });
});
