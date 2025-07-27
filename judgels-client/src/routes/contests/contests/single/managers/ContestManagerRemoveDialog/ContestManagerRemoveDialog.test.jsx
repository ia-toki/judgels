import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ContestManagerRemoveDialog } from './ContestManagerRemoveDialog';

describe('ContestManagerRemoveDialog', () => {
  let onDeleteManagers;
  let wrapper;

  beforeEach(() => {
    onDeleteManagers = jest.fn().mockReturnValue(Promise.resolve({ deletedManagerProfilesMap: {} }));

    const store = configureMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      onDeleteManagers,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestManagerRemoveDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.prop('onChange')({ target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onDeleteManagers).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
