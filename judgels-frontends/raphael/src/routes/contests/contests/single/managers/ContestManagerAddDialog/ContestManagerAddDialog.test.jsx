import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ContestManagerAddDialog } from './ContestManagerAddDialog';

describe('ContestManagerAddDialog', () => {
  let onUpsertManagers;
  let wrapper;

  beforeEach(() => {
    onUpsertManagers = jest
      .fn()
      .mockReturnValue(Promise.resolve({ insertedManagerProfilesMap: {}, alreadyManagerProfilesMap: {} }));

    const store = configureMockStore()({});

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

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.getDOMNode().value = 'andi\n\nbudi\n caca  \n';
    usernames.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpsertManagers).toHaveBeenCalledWith('contestJid', ['andi', 'budi', 'caca']);
  });
});
