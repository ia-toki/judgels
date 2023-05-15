import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { push } from 'connected-react-router';
import createMockStore from 'redux-mock-store';
import { stringify } from 'query-string';

import SearchBox from './SearchBox';

describe('SearchBox', () => {
  let wrapper;
  let onRouteChange;

  const store = createMockStore()({});

  const render = (key, initialValue) => {
    const props = {
      initialValue,
      onRouteChange,
    };
    const component = () => <SearchBox {...props} />;

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={[`/component?${key}=${initialValue}&page=2`]}>
          <Route path="/component" component={component} />
        </MemoryRouter>
      </Provider>
    );
  };

  const submit = value => {
    const content = wrapper.find('input[name="content"]');
    content.getDOMNode().value = value;
    content.simulate('input');
    wrapper.find('form').simulate('submit');
  };

  describe('when onSubmit is invoked by enter key or button press', () => {
    beforeEach(() => {
      onRouteChange = jest.fn().mockReturnValue({ key: 'judgels' });
    });

    it('updates the query string', () => {
      render('key', 'test');
      submit('judgels');
      const query = stringify({ key: 'judgels' });
      expect(store.getActions()).toContainEqual(push({ search: query }));
    });

    it('calls onRouteChange with correct previous route and the typed string', () => {
      render('key', 'test');
      submit('judgels');
      expect(onRouteChange).toBeCalledWith('judgels', { key: 'test', page: '2' });
    });
  });
});
