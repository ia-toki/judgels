import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { SupervisorManagementPermission } from '../../../../../../modules/api/uriel/contestSupervisor';
import { ContestSupervisorAddDialog } from './ContestSupervisorAddDialog';

describe('ContestSupervisorAddDialog', () => {
  let onUpsertSupervisors;
  let wrapper;

  beforeEach(() => {
    onUpsertSupervisors = jest
      .fn()
      .mockReturnValue(Promise.resolve({ upsertedSupervisorProfilesMap: {}, alreadySupervisorProfilesMap: {} }));

    const store = configureMockStore()({});

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

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.prop('onChange')({ target: { value: 'andi\n\nbudi\n caca  \n' } });

    const announcementPermission = wrapper.find('input[name="managementPermissions.Announcements"]');
    announcementPermission.getDOMNode().checked = true;
    announcementPermission.simulate('change');

    const clarificationPermission = wrapper.find('input[name="managementPermissions.Clarifications"]');
    clarificationPermission.getDOMNode().checked = true;
    clarificationPermission.simulate('change');

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
