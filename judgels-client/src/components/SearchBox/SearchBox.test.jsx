import { RouterProvider, useLocation } from '@tanstack/react-router';
import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import { createTestRouter } from '../../test/RouterWrapper';
import SearchBox from './SearchBox';

describe('SearchBox', () => {
  let testLocation;

  const LocationTracker = () => {
    const location = useLocation();
    testLocation = location;
    return null;
  };

  const renderComponent = async ({ key, initialValue, onRouteChange }) => {
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

    await act(async () => render(<RouterProvider router={router} />));
  };

  const submit = async value => {
    const user = userEvent.setup();
    const content = await screen.findByRole('textbox');
    await user.clear(content);
    await user.type(content, value);
    const submitButton = screen.getByRole('button');
    await user.click(submitButton);
  };

  test('updates the query string', async () => {
    await renderComponent({
      key: 'key',
      initialValue: 'test',
      onRouteChange: vi.fn().mockReturnValue({ key: 'judgels' }),
    });
    await submit('judgels');
    expect(testLocation.search).toEqual({ key: 'judgels' });
  });

  test('calls onRouteChange with correct previous route and the typed string', async () => {
    const onRouteChange = vi.fn().mockReturnValue({ key: 'judgels' });
    await renderComponent({ key: 'key', initialValue: 'test', onRouteChange });
    await submit('judgels');
    expect(onRouteChange).toBeCalledWith('judgels', { key: 'test', page: '2' });
  });
});
