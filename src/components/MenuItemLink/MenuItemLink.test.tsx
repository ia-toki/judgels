import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { push } from 'react-router-redux';
import createMockStore, { MockStore } from 'redux-mock-store';

import MenuItemLink from './MenuItemLink';

describe('MenuItemLink', () => {
  let wrapper: ReactWrapper<any, any>;
  let store: MockStore<any>;

  beforeEach(() => {
    store = createMockStore()({});
    wrapper = mount(
      <Provider store={store}>
        <MenuItemLink text="Account" to="/account" />
      </Provider>
    );
  });

  it('shows the text', () => {
    expect(wrapper.text()).toEqual('Account');
  });

  it('pushes new location when clicked', () => {
    wrapper.find('a').simulate('click');
    expect(store.getActions()).toContainEqual(push('/account'));
  });
});
