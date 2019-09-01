import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { push } from 'connected-react-router';
import { createStore, combineReducers, Store } from 'redux';
import { reducer as formReducer } from 'redux-form';
import createMockStore, { MockStore } from 'redux-mock-store';
import { stringify } from 'query-string';

import { AppState } from '../../modules/store';

import SearchBox, { SearchBoxProps } from './SearchBox';

describe('SearchBox', () => {
  let wrapper: ReactWrapper<any, any>;
  let onRouteChange: jest.Mock<any>;
  let mockStore: MockStore<Partial<AppState>>;

  const realStore = createStore(combineReducers({ form: formReducer }));

  const render = (store: any, key: string, initialValue: string) => {
    const props: SearchBoxProps = {
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

  const submit = (content: string) => {
    wrapper.find('input[name="content"]').simulate('change', { target: { value: content } });
    wrapper.find('form').simulate('submit');
  };

  describe('when onSubmit is invoked by enter key or button press', () => {
    beforeEach(() => {
      onRouteChange = jest.fn().mockReturnValue({ key: 'judgels' });
    });

    it('updates the query string', () => {
      mockStore = createMockStore<Partial<AppState>>()({});
      render(mockStore, 'key', 'test');
      submit('judgels');
      const query = stringify({ key: 'judgels' });
      expect(mockStore.getActions()).toContainEqual(push({ search: query }));
    });

    it('calls onRouteChange with correct previous route and the typed string', () => {
      render(realStore, 'key', 'test');
      submit('judgels');
      expect(onRouteChange).toBeCalledWith('judgels', { key: 'test', page: '2' });
    });
  });
});
