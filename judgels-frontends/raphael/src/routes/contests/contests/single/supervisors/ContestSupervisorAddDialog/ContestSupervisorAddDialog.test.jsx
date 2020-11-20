import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';
import { SupervisorManagementPermission } from '../../../../../../modules/api/uriel/contestSupervisor';
import { ContestSupervisorAddDialog } from './ContestSupervisorAddDialog';

describe('ContestSupervisorAddDialog', () => {
  let onUpsertSupervisors;
  let wrapper;

  beforeEach(() => {
    onUpsertSupervisors = jest
      .fn()
      .mockReturnValue(Promise.resolve({ upsertedSupervisorProfilesMap: {}, alreadySupervisorProfilesMap: {} }));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest,
      onUpsertSupervisors: onUpsertSupervisors,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestSupervisorAddDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('add supervisors dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const announcementPermission = wrapper.find('input[name="managementPermissions.Announcements"]');
    announcementPermission.simulate('change', { target: { checked: true } });

    const clarificationPermission = wrapper.find('input[name="managementPermissions.Clarifications"]');
    clarificationPermission.simulate('change', { target: { checked: true } });

    wrapper.update();

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertSupervisors).toHaveBeenCalledWith(contestJid, {
      managementPermissions: [
        SupervisorManagementPermission.Announcements,
        SupervisorManagementPermission.Clarifications,
      ],
      usernames: ['andi', 'budi', 'caca'],
    });
  });
});
