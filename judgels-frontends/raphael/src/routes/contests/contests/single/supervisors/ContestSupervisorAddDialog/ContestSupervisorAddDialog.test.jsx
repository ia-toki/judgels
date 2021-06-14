import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

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
      contest: { jid: 'contestJid' },
      onUpsertSupervisors: onUpsertSupervisors,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestSupervisorAddDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.getDOMNode().value = 'andi\n\nbudi\n caca  \n';
    usernames.simulate('input');

    const announcementPermission = wrapper.find('input[name="managementPermissions.Announcements"]');
    announcementPermission.getDOMNode().checked = true;
    announcementPermission.simulate('change');

    const clarificationPermission = wrapper.find('input[name="managementPermissions.Clarifications"]');
    clarificationPermission.getDOMNode().checked = true;
    clarificationPermission.simulate('change');

    wrapper.update();

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertSupervisors).toHaveBeenCalledWith('contestJid', {
      managementPermissions: [
        SupervisorManagementPermission.Announcements,
        SupervisorManagementPermission.Clarifications,
      ],
      usernames: ['andi', 'budi', 'caca'],
    });
  });
});
