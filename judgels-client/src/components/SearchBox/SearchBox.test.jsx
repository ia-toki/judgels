import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { push } from 'connected-react-router';
import { stringify } from 'query-string';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import createMockStore from 'redux-mock-store';

import SearchBox from './SearchBox';

describe('SearchBox', () => {
  let onRouteChange;

  const store = createMockStore()({});

  const renderComponent = (key, initialValue) => {
    const props = {
      initialValue,
      onRouteChange,
    };
    const component = () => <SearchBox {...props} />;

    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={[`/component?${key}=${initialValue}&page=2`]}>
          <Route path="/component" component={component} />
        </MemoryRouter>
      </Provider>
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
      onRouteChange = jest.fn().mockReturnValue({ key: 'judgels' });
    });

    it('updates the query string', async () => {
      renderComponent('key', 'test');
      await submit('judgels');
      const query = stringify({ key: 'judgels' });
      expect(store.getActions()).toContainEqual(push({ search: query }));
    });

    it('calls onRouteChange with correct previous route and the typed string', async () => {
      renderComponent('key', 'test');
      await submit('judgels');
      expect(onRouteChange).toBeCalledWith('judgels', { key: 'test', page: '2' });
    });
  });
});
