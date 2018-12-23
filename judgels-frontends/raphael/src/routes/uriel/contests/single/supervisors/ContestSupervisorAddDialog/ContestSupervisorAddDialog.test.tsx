import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from 'fixtures/state';
import { ContestSupervisorUpsertData } from 'modules/api/uriel/contestSupervisor';
import { SupervisorManagementPermission } from 'modules/api/uriel/contestSupervisor';

import { ContestSupervisorAddDialog, ContestSupervisorAddDialogProps } from './ContestSupervisorAddDialog';

describe('ContestSupervisorAddDialog', () => {
  let onUpsertSupervisors: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpsertSupervisors = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }));

    const props: ContestSupervisorAddDialogProps = {
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

    const announcementPermission = wrapper.find('input[name="managementPermissions.Announcement"]');
    announcementPermission.simulate('change', { target: { checked: false } });

    const clarificationPermission = wrapper.find('input[name="managementPermissions.Clarification"]');
    clarificationPermission.simulate('change', { target: { checked: false } });

    wrapper.update();

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertSupervisors).toHaveBeenCalledWith(contestJid, {
      managementPermissions: [
        SupervisorManagementPermission.Problem,
        SupervisorManagementPermission.Submission,
        SupervisorManagementPermission.Team,
        SupervisorManagementPermission.Scoreboard,
        SupervisorManagementPermission.File,
      ],
      usernames: ['andi', 'budi', 'caca'],
    } as ContestSupervisorUpsertData);
  });
});
