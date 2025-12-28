import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { stringify } from 'query-string';
import { MemoryRouter, Route, Routes, useLocation } from 'react-router-dom';
import { vi } from 'vitest';

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

    render(
      <MemoryRouter initialEntries={[`/component?${key}=${initialValue}&page=2`]}>
        <Routes>
          <Route
            path="/component"
            element={
              <>
                <SearchBox {...props} />
                <LocationTracker />
              </>
            }
          />
        </Routes>
      </MemoryRouter>
    );
  };

  const submit = async value => {
    const user = userEvent.setup();
    const content = screen.getByRole('textbox');
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
      const query = stringify({ key: 'judgels' });
      expect(testLocation.search).toBe('?' + query);
    });

    it('calls onRouteChange with correct previous route and the typed string', async () => {
      renderComponent('key', 'test');
      await submit('judgels');
      expect(onRouteChange).toBeCalledWith('judgels', { key: 'test', page: '2' });
    });
  });
});
