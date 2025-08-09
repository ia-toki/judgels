import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ContestSupervisorRemoveDialog } from './ContestSupervisorRemoveDialog';

describe('ContestSupervisorRemoveDialog', () => {
  let onDeleteSupervisors;
  let wrapper;

  beforeEach(() => {
    onDeleteSupervisors = jest.fn().mockReturnValue(Promise.resolve({ deletedSupervisorProfilesMap: {} }));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteSupervisors: onDeleteSupervisors,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestSupervisorRemoveDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.prop('onChange')({ target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onDeleteSupervisors).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
