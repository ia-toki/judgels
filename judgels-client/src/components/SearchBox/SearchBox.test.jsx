import { RouterProvider, useLocation } from '@tanstack/react-router';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { createTestRouter } from '../../test/RouterWrapper';
import SearchBox from './SearchBox';

describe('SearchBox', () => {
  let onRouteChange;
  let testLocation;

  const LocationTracker = () => {
    const location = useLocation();
    testLocation = location;
    return null;
  };

  const renderComponent = (key, initialValue) => {
    const props = {
      initialValue,
      onRouteChange,
    };

    const router = createTestRouter(
      () => (
        <>
          <SearchBox {...props} />
          <LocationTracker />
        </>
      ),
      [`/component?${key}=${initialValue}&page=2`]
    );

    render(<RouterProvider router={router} />);
  };

  const submit = async value => {
    const user = userEvent.setup();
    const content = await screen.findByRole('textbox');
    await user.clear(content);
    await user.type(content, value);
    const submitButton = screen.getByRole('button');
    await user.click(submitButton);
  };

  describe('when onSubmit is invoked by enter key or button press', () => {
    beforeEach(() => {
      onRouteChange = vi.fn().mockReturnValue({ key: 'judgels' });
    });

    it('updates the query string', async () => {
      renderComponent('key', 'test');
      await submit('judgels');
      expect(testLocation.search).toEqual({ key: 'judgels' });
    });

    it('calls onRouteChange with correct previous route and the typed string', async () => {
      renderComponent('key', 'test');
      await submit('judgels');
      expect(onRouteChange).toBeCalledWith('judgels', { key: 'test', page: '2' });
    });
  });
});
