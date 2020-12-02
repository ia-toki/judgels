import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ContestContestantAddDialog } from './ContestContestantAddDialog';

describe('ContestContestantAddDialog', () => {
  let onUpsertContestants;
  let wrapper;

  beforeEach(() => {
    onUpsertContestants = jest
      .fn()
      .mockReturnValue(Promise.resolve({ insertedContestantProfilesMap: {}, alreadyContestantProfilesMap: {} }));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest: { jid: 'contestJid' },
      onUpsertContestants,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestContestantAddDialog {...props} />
        </MemoryRouter>
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

    expect(onUpsertContestants).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
