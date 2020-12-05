import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ContestManagerAddDialog } from './ContestManagerAddDialog';

describe('ContestManagerAddDialog', () => {
  let onUpsertManagers;
  let wrapper;

  beforeEach(() => {
    onUpsertManagers = jest
      .fn()
      .mockReturnValue(Promise.resolve({ insertedManagerProfilesMap: {}, alreadyManagerProfilesMap: {} }));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest: { jid: 'contestJid' },
      onUpsertManagers,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestManagerAddDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertManagers).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
