import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';

import { ContestSupervisorRemoveDialog } from './ContestSupervisorRemoveDialog';

describe('ContestSupervisorRemoveDialog', () => {
  let onDeleteSupervisors;
  let wrapper;

  beforeEach(() => {
    onDeleteSupervisors = jest.fn().mockReturnValue(Promise.resolve({ deletedSupervisorProfilesMap: {} }));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest,
      onDeleteSupervisors: onDeleteSupervisors,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestSupervisorRemoveDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('remove supervisors dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onDeleteSupervisors).toHaveBeenCalledWith(contestJid, ['andi', 'budi', 'caca']);
  });
});
